# Week 14: 클라우드 컴퓨팅 종합 실습

## 실습 개요

이번 실습에서는 Docker Compose로 시작하여 Kubernetes로 배포하고, Prometheus와 Grafana를 통해 모니터링하는 전체 워크플로우를 경험합니다.

**실습 시간:** 100분  
**난이도:** 중급  
**준비물:** Docker, Minikube, kubectl 설치 완료

---

## 사전 준비

### 환경 확인

```bash
# Docker 버전 확인
docker --version

# Minikube 상태 확인
minikube status

# kubectl 버전 확인
kubectl version --client
```

### 작업 디렉토리 생성

```bash
mkdir -p ~/week14-final-lab
cd ~/week14-final-lab
```

---

## Phase 1: Docker Compose로 애플리케이션 구성 (20분)

### 1-1. Flask 애플리케이션 작성

```bash
# 애플리케이션 디렉토리 생성
mkdir -p app
cd app
```

**app.py 파일 생성:**

```bash
cat > app.py << 'EOF'
from flask import Flask, jsonify, request
from prometheus_client import Counter, Histogram, Gauge, generate_latest
import time

app = Flask(__name__)

# Prometheus 메트릭 정의
requests_total = Counter(
    'flask_requests_total',
    'Total requests',
    ['method', 'endpoint', 'status']
)

request_duration = Histogram(
    'flask_request_duration_seconds',
    'Request duration',
    ['method', 'endpoint']
)

deployment_total = Gauge('deployment_total', 'Total deployments')
active_requests = Gauge('flask_active_requests', 'Active requests')

@app.before_request
def before_request():
    request.start_time = time.time()
    active_requests.inc()

@app.after_request
def after_request(response):
    duration = time.time() - request.start_time
    requests_total.labels(
        method=request.method,
        endpoint=request.endpoint or 'unknown',
        status=response.status_code
    ).inc()
    request_duration.labels(
        method=request.method,
        endpoint=request.endpoint or 'unknown'
    ).observe(duration)
    active_requests.dec()
    return response

@app.route('/api/hello', methods=['GET'])
def hello():
    return jsonify({'message': 'Hello, Cloud Computing!', 'timestamp': time.time()})

@app.route('/api/process', methods=['POST'])
def process():
    data = request.get_json()
    time.sleep(0.5)
    return jsonify({'result': 'processed', 'data': data})

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'healthy'}), 200

@app.route('/ready', methods=['GET'])
def ready():
    return jsonify({'status': 'ready'}), 200

@app.route('/metrics', methods=['GET'])
def metrics():
    return generate_latest(), 200, {'Content-Type': 'text/plain; charset=utf-8'}

@app.route('/error', methods=['GET'])
def error():
    return jsonify({'error': 'Simulated error'}), 500

if __name__ == '__main__':
    deployment_total.set(1)
    app.run(host='0.0.0.0', port=5000, debug=True)
EOF
```

**Dockerfile 생성:**

```bash
cat > Dockerfile << 'EOF'
FROM python:3.9-slim

WORKDIR /app

RUN pip install flask prometheus-client psycopg2-binary redis

COPY app.py .

EXPOSE 5000

CMD ["python", "app.py"]
EOF
```

### 1-2. Prometheus 설정 파일 작성

```bash
cd ~/week14-final-lab

cat > prometheus.yml << 'EOF'
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'flask-app'
    static_configs:
      - targets: ['app:5000']

  - job_name: 'postgres'
    static_configs:
      - targets: ['db:5432']
EOF
```

### 1-3. Docker Compose 파일 작성

```bash
cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  app:
    build: ./app
    ports:
      - "5000:5000"
    environment:
      FLASK_APP: app.py
      FLASK_ENV: development
    depends_on:
      - db
      - redis
    networks:
      - app-net

  db:
    image: postgres:13-alpine
    environment:
      POSTGRES_DB: myapp
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: secret
    volumes:
      - db_data:/var/lib/postgresql/data
    networks:
      - app-net

  redis:
    image: redis:7-alpine
    networks:
      - app-net

  prometheus:
    image: prom/prometheus:latest
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - prometheus_data:/prometheus
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
    networks:
      - app-net

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    volumes:
      - grafana_data:/var/lib/grafana
    depends_on:
      - prometheus
    networks:
      - app-net

volumes:
  db_data:
  prometheus_data:
  grafana_data:

networks:
  app-net:
    driver: bridge
EOF
```

### 1-4. 애플리케이션 시작

```bash
# Docker Compose로 모든 서비스 시작
docker-compose up -d

# 서비스 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs app
```

### 1-5. 애플리케이션 테스트

```bash
# 헬스 체크
curl http://localhost:5000/health

# API 테스트
curl http://localhost:5000/api/hello

# 메트릭 확인
curl http://localhost:5000/metrics
```

**✅ Phase 1 완료 체크:**
- [ ] Docker Compose 서비스가 모두 Running 상태인가?
- [ ] Flask 앱이 5000번 포트에서 응답하는가?
- [ ] `/metrics` 엔드포인트에서 데이터가 나오는가?

---

## Phase 2: Kubernetes 배포 (25분)

### 2-1. Minikube 시작

```bash
# Minikube 시작 (이미 실행 중이면 스킵)
minikube start --memory=4096 --cpus=2

# 상태 확인
minikube status
```

### 2-2. Docker 이미지 빌드

```bash
cd ~/week14-final-lab

# Docker 이미지 빌드
docker build -t flask-app:latest ./app

# Minikube에 이미지 로드
minikube image load flask-app:latest

# 이미지 확인
minikube image ls | grep flask-app
```

### 2-3. Kubernetes Deployment 파일 작성

```bash
cat > k8s-deployment.yaml << 'EOF'
apiVersion: apps/v1
kind: Deployment
metadata:
  name: flask-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: flask-app
  template:
    metadata:
      labels:
        app: flask-app
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "5000"
        prometheus.io/path: "/metrics"
    spec:
      containers:
      - name: app
        image: flask-app:latest
        imagePullPolicy: Never
        ports:
        - containerPort: 5000
        livenessProbe:
          httpGet:
            path: /health
            port: 5000
          initialDelaySeconds: 10
          periodSeconds: 5
        readinessProbe:
          httpGet:
            path: /ready
            port: 5000
          initialDelaySeconds: 5
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: flask-app-svc
spec:
  type: NodePort
  ports:
  - port: 5000
    targetPort: 5000
    nodePort: 30500
  selector:
    app: flask-app
EOF
```

### 2-4. Kubernetes에 배포

```bash
# Deployment 적용
kubectl apply -f k8s-deployment.yaml

# Pod 상태 확인
kubectl get pods

# Pod가 Running 상태가 될 때까지 대기
kubectl get pods -w
# Ctrl+C로 종료

# Service 확인
kubectl get svc

# Deployment 확인
kubectl get deployments
```

### 2-5. 애플리케이션 접근

```bash
# Minikube 서비스 URL 확인
minikube service flask-app-svc --url

# 또는 포트 포워딩
kubectl port-forward svc/flask-app-svc 5000:5000 &

# 테스트
curl http://localhost:5000/api/hello
```

**✅ Phase 2 완료 체크:**
- [ ] Pod가 3개 모두 Running 상태인가?
- [ ] Service가 정상적으로 생성되었는가?
- [ ] 애플리케이션에 접근 가능한가?

---

## Phase 3: Prometheus 메트릭 수집 (15분)

### 3-1. Prometheus 접속

```bash
# 브라우저에서 접속
http://localhost:9090
```

### 3-2. 메트릭 조회 실습

**쿼리 입력창에 다음 쿼리들을 입력하고 "Execute" 클릭:**

1. **현재 요청 수:**
```
flask_requests_total
```

2. **요청률 (초당):**
```
rate(flask_requests_total[5m])
```

3. **에러율:**
```
rate(flask_requests_total{status=~"5.."}[5m])
```

4. **활성 요청 수:**
```
flask_active_requests
```

### 3-3. 그래프 확인

- "Graph" 탭을 클릭하여 시간별 변화 확인
- 시간 범위 조정: 5분, 15분, 1시간
- 자동 새로고침 설정

### 3-4. 부하 테스트 (별도 터미널)

```bash
# 트래픽 생성
while true; do
  curl http://localhost:5000/api/hello
  sleep 1
done
```

**✅ Phase 3 완료 체크:**
- [ ] Prometheus에서 메트릭이 조회되는가?
- [ ] 그래프가 실시간으로 업데이트되는가?
- [ ] 부하 테스트 시 요청률이 증가하는가?

---

## Phase 4: Grafana 대시보드 구축 (25분)

### 4-1. Grafana 접속

```bash
# 브라우저에서 접속
http://localhost:3000

# 로그인 정보
username: admin
password: admin
```

### 4-2. Prometheus 데이터 소스 추가

1. 좌측 메뉴에서 "Connections" 또는 "Configuration" 클릭
2. "Data Sources" 선택
3. "Add data source" 버튼 클릭
4. "Prometheus" 선택
5. 설정:
   - **Name:** Prometheus
   - **URL:** http://prometheus:9090
6. 하단 "Save & test" 클릭
7. "Data source is working" 메시지 확인

### 4-3. 대시보드 생성

1. 좌측 메뉴 → "Dashboards"
2. "New" → "New Dashboard" 클릭
3. "Add visualization" 클릭
4. 데이터 소스로 "Prometheus" 선택

### 4-4. 패널 추가

#### 패널 1: 요청률 (RPS)

- **쿼리:** `rate(flask_requests_total[5m])`
- **Title:** 요청률 (RPS)
- **Visualization:** Time series
- **Apply** 클릭

#### 패널 2: 에러율

- "Add" → "Visualization" 클릭
- **쿼리:** `rate(flask_requests_total{status=~"5.."}[5m])`
- **Title:** 에러율
- **Visualization:** Gauge
- Threshold 설정:
  - 녹색: 0 - 0.05
  - 주황색: 0.05 - 0.1
  - 빨강색: 0.1+
- **Apply** 클릭

#### 패널 3: 활성 요청 수

- "Add" → "Visualization" 클릭
- **쿼리:** `flask_active_requests`
- **Title:** 활성 요청 수
- **Visualization:** Stat
- **Apply** 클릭

### 4-5. 대시보드 저장

1. 우측 상단 "Save dashboard" 아이콘 클릭
2. 이름 입력: "Week14 모니터링 대시보드"
3. "Save" 클릭

### 4-6. 자동 새로고침 설정

- 우측 상단 시간 범위 옆 드롭다운
- "5s" 또는 "10s" 선택

**✅ Phase 4 완료 체크:**
- [ ] Prometheus 데이터 소스가 정상 연결되었는가?
- [ ] 대시보드에 3개 패널이 추가되었는가?
- [ ] 실시간 데이터가 표시되는가?

---

## Phase 5: 통합 테스트 및 모니터링 (10분)

### 5-1. 부하 테스트 시작

**터미널 1: 정상 트래픽**
```bash
while true; do
  curl http://localhost:5000/api/hello
  sleep 0.5
done
```

**터미널 2: 에러 트래픽**
```bash
while true; do
  curl http://localhost:5000/error
  sleep 3
done
```

### 5-2. Prometheus에서 확인

1. http://localhost:9090 접속
2. 쿼리 입력: `rate(flask_requests_total[1m])`
3. Graph 탭에서 요청률 증가 확인

### 5-3. Grafana 대시보드 확인

1. http://localhost:3000 접속
2. 생성한 대시보드 열기
3. 실시간 데이터 변화 관찰:
   - 요청률 그래프 상승
   - 에러율 게이지 색상 변화
   - 활성 요청 수 변동

### 5-4. Kubernetes Pod 확인

```bash
# Pod 상태
kubectl get pods

# 실시간 로그
kubectl logs -f deployment/flask-app

# Pod 리소스 사용량
kubectl top pods
```

**✅ Phase 5 완료 체크:**
- [ ] 부하 테스트가 정상 실행되는가?
- [ ] Prometheus에서 메트릭 증가가 관찰되는가?
- [ ] Grafana 대시보드가 실시간 업데이트되는가?
- [ ] Pod 로그에서 요청이 확인되는가?

---

## 실습 정리

### 서비스 중지

```bash
# Docker Compose 중지
cd ~/week14-final-lab
docker-compose down

# Kubernetes 리소스 삭제
kubectl delete -f k8s-deployment.yaml

# Minikube 중지 (선택사항)
minikube stop
```

### 디렉토리 구조 확인

```bash
tree ~/week14-final-lab

# 예상 구조:
# week14-final-lab/
# ├── app/
# │   ├── app.py
# │   └── Dockerfile
# ├── docker-compose.yml
# ├── prometheus.yml
# └── k8s-deployment.yaml
```

---

## 학습 목표 달성 확인

다음 질문에 답할 수 있으면 실습이 성공적으로 완료된 것입니다:

1. Docker Compose와 Kubernetes의 차이점은 무엇인가?
2. Prometheus는 어떤 방식으로 메트릭을 수집하는가?
3. Grafana에서 시각화의 장점은 무엇인가?
4. Liveness Probe와 Readiness Probe의 차이는?
5. Service의 NodePort와 LoadBalancer의 차이는?

---

## 추가 도전 과제 (선택)

시간이 남는다면 다음을 시도해 보세요:

1. **Horizontal Pod Autoscaler 추가:**
```bash
kubectl autoscale deployment flask-app --cpu-percent=50 --min=3 --max=10
```

2. **ConfigMap으로 설정 분리:**
```bash
kubectl create configmap app-config --from-literal=FLASK_ENV=production
```

3. **Grafana 알림 규칙 추가:**
   - 에러율이 10%를 초과하면 알림

4. **Rolling Update 테스트:**
```bash
# 이미지 업데이트
kubectl set image deployment/flask-app app=flask-app:v2
```

---

## 트러블슈팅

### 문제 1: Pod가 Pending 상태

**원인:** 이미지를 찾을 수 없거나 리소스 부족

**해결:**
```bash
kubectl describe pod <pod-name>
minikube image load flask-app:latest
```

### 문제 2: Prometheus에서 메트릭이 안 보임

**원인:** Flask 앱의 /metrics 엔드포인트 문제

**해결:**
```bash
curl http://localhost:5000/metrics
docker-compose restart app
```

### 문제 3: Grafana에서 데이터 없음

**원인:** Prometheus 데이터 소스 연결 문제

**해결:**
- Data Sources에서 "Test" 버튼 클릭
- URL이 http://prometheus:9090 인지 확인

---

**실습 완료를 축하합니다! 🎉**
