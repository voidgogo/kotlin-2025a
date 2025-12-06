# Week 14: 클라우드 컴퓨팅 종합 실습 - 교사용 해설본

## 1. 수업 목표 및 포인트

### 1-1. 학습 목표

이 실습의 핵심 목표는 다음 세 가지입니다.

1. **컨테이너화된 애플리케이션을 Docker Compose로 구성**하고, 이를 **Kubernetes로 이식**하는 경험
2. 애플리케이션에 **Prometheus 메트릭을 내장**하고, **Grafana로 시각화**하는 전체 모니터링 플로우 이해
3. 실습을 통해 DevOps/운영 관점에서 **관찰 가능성(Observability)** 의 기본 개념을 체득

### 1-2. 학생들이 헷갈릴 수 있는 지점

- Docker Compose의 네트워크 이름(`app-net`)과 Kubernetes Service DNS가 서로 다르다는 점
- Minikube 안/밖에서 사용하는 Docker 이미지 빌드 방식 차이
- Prometheus에서 `rate()` 함수의 의미
- Grafana에서 Data Source 경로(`http://prometheus:9090`)와 실제 브라우저 접속 URL(`http://localhost:3000`) 혼동

---

## 2. 전체 타임라인 가이드 (총 100분 기준)

| Phase | 내용 | 권장 시간 | 교사 역할 |
|-------|------|-----------|-----------|
| 1 | Docker Compose로 로컬 앱 구성 | 20분 | 템플릿 제공, 에러 핸들링 지원 |
| 2 | Kubernetes 배포 및 포트 포워딩 | 25분 | 이미지 빌드/로드 개념 설명 |
| 3 | Prometheus 메트릭 조회 | 15분 | 기본 쿼리 실습 가이드 |
| 4 | Grafana 대시보드 생성 | 25분 | 패널 예시 2~3개 시연 후 자율 구성 유도 |
| 5 | 통합 테스트 및 토론 | 10분 | 결과 공유, 질의응답 |

**TIP:** 실습 속도 차이가 크므로, 각 Phase 끝에 **"필수 체크포인트"** 를 짧게 확인하고 넘어가는 것이 좋습니다.

---

## 3. Phase별 해설 및 체크포인트

### 3-1. Phase 1: Docker Compose로 애플리케이션 구성

#### 핵심 의도

- 모든 학생이 **동일한 초기 상태**에서 시작하도록 로컬 환경을 통일
- Flask 앱 내부에 **Prometheus 메트릭 노출 패턴**을 보여주는 것이 핵심

#### 교사 설명 포인트

1. `prometheus_client` 라이브러리를 통해
   - Counter: `flask_requests_total`
   - Histogram: `flask_request_duration_seconds`
   - Gauge: `deployment_total`, `flask_active_requests`
   를 정의하고 사용하는 패턴을 간단히 짚어줍니다.

2. `@before_request` / `@after_request` 훅을 통해
   - 요청 시작 시점과 종료 시점에서 메트릭을 업데이트하는 구조를 강조합니다.

3. `/metrics` 엔드포인트가 Prometheus의 **scrape 대상**이 된다는 점을 명확히 설명합니다.

#### 자주 나오는 문제 & 해결

- **문제:** `ModuleNotFoundError: No module named 'flask'`
  - **원인:** Docker 이미지 빌드 전에 로컬에서 직접 `python app.py` 실행
  - **해결:** 학생에게 로컬 실행은 하지 말고, 반드시 Docker를 통해 실행하도록 안내

- **문제:** `docker-compose: command not found`
  - **해결:** `docker compose` vs `docker-compose` 차이 설명 (실습에서는 `docker-compose` 또는 설치된 형태에 맞춰 통일)

- **문제:** 포트 충돌 (이미 5000, 9090, 3000을 쓰는 프로세스가 있는 경우)
  - **해결:** 
    ```bash
    sudo lsof -i:5000
    sudo lsof -i:9090
    sudo lsof -i:3000
    ```
    로 확인 후 프로세스 종료 유도

#### 체크포인트 (Phase 1 끝)

- `docker-compose ps` 에서 5개 서비스(app, db, redis, prometheus, grafana)가 **모두 Up 상태**인지 확인
- 브라우저에서 `http://localhost:5000/api/hello` 호출 시 JSON 응답이 오는지 확인
- `http://localhost:5000/metrics` 에 텍스트 메트릭이 노출되는지 확인

---

### 3-2. Phase 2: Kubernetes 배포

#### 핵심 의도

- Docker Compose로 구성한 앱을 **Kubernetes 환경으로 이식하는 흐름** 경험
- Minikube의 **로컬 이미지 사용 패턴(minikube image load)** 을 익히는 것이 포인트

#### 교사 설명 포인트

1. `imagePullPolicy: Never` 를 사용하는 이유:
   - Minikube 클러스터가 Docker Hub에서 이미지를 당겨 오는 것이 아니라, **로컬에 빌드한 이미지를 사용**하게 하기 위함

2. `Deployment` vs `Service` 역할 강조:
   - Deployment: Pod의 수, 업데이트 전략 관리
   - Service(NodePort): 외부에서 접근 가능한 고정 엔드포인트 제공

3. `minikube image load` 의 의미:
   - Minikube의 Docker 데몬과 호스트의 Docker 데몬이 **다를 수 있다**는 점을 짧게 짚어줍니다.

#### 자주 나오는 문제 & 해결

- **문제:** `kubectl port-forward svc/flask-app-svc 5000:5000` 시 `pod is not running. Current status=Pending`
  - **해결 절차:**
    ```bash
    kubectl get pods
    kubectl describe pod <파드-이름>
    ```
    - `ImagePullBackOff` → `minikube image load flask-app:latest` 다시 실행
    - `CrashLoopBackOff` → `kubectl logs <파드-이름>` 로 Python 에러 확인

- **문제:** Service는 있는데 `minikube service flask-app-svc --url` 로 접속이 안 됨
  - **원인:** Pod가 Ready 상태가 아님
  - **해결:** `kubectl get pods` 로 Ready 컬럼 `1/1`, `3/3` 여부 확인

#### 체크포인트 (Phase 2 끝)

- `kubectl get pods` 에서 `flask-app` 파드 3개가 모두 `Running` 상태인지 확인
- `kubectl get svc` 에서 `flask-app-svc` 가 `NodePort` 로 떠 있는지 확인
- `curl http://localhost:5000/api/hello` (포트포워딩 후) 가 정상 응답인지 확인

---

### 3-3. Phase 3: Prometheus 메트릭 조회

#### 핵심 의도

- 단순 모니터링이 아니라, **메트릭 쿼리 관점**을 체득하게 하는 것이 목표

#### 교사 설명 포인트

1. `rate(flask_requests_total[5m])` 의 의미:
   - 5분 동안의 증가량을 초 단위로 나눈 값 (per-second rate)
   - 실무에서 **RPS(Requests Per Second)** 를 보는 가장 기본적인 방식

2. 라벨 기반 필터링 예시:
   - `flask_requests_total{status="200"}`
   - `flask_requests_total{endpoint="/api/hello"}`

3. Graph 탭에서 시간 범위 조정 & 확대/축소 시연

#### 자주 나오는 문제 & 해결

- **문제:** 메트릭 이름 자동완성이 안 뜸
  - **해결:** 트래픽이 없으면 메트릭이 생성되지 않을 수 있으므로, 먼저 여러 번 API를 호출시키고 다시 시도하게 함

- **문제:** `flask_requests_total` 조회 시 아무 값도 안 나옴
  - **해결:**
    - `/metrics` 에 값이 실제로 나오는지 확인
    - Prometheus `prometheus.yml` 의 `targets: ['app:5000']` 가 올바른지 확인

#### 체크포인트 (Phase 3 끝)

- 최소 1개 이상 PromQL 쿼리 결과를 그래프로 직접 확인했는지
- RPS와 에러율의 개념을 말로 설명할 수 있는지 간단히 질문

---

### 3-4. Phase 4: Grafana 대시보드 생성

#### 핵심 의도

- **개인별로 서로 다른 대시보드**를 만들어 보게 해서, UI 익숙도를 높이는 것

#### 교사 설명 포인트

1. Data Source 설정 시 **브라우저 기준 URL이 아니라 컨테이너 네트워크 기준** 이라는 점을 강조:
   - 브라우저는 `http://localhost:3000` 으로 Grafana에 접속하지만,
   - Grafana 컨테이너 입장에서 Prometheus는 `http://prometheus:9090` 으로 접근

2. 패널 생성 최소 예시 2개만 라이브로 보여주고, 나머지는 학생들이 자유롭게 구성하도록 유도:
   - 요청률 (Time series)
   - 에러율 (Gauge)

3. 시간 범위와 자동 새로고침 설정을 통해 **실시간 모니터링 느낌** 을 주는 것이 좋음

#### 자주 나오는 문제 & 해결

- **문제:** Data source test 실패
  - **해결:**
    - URL 오타 (`http://prometheus:9090`)
    - Prometheus 컨테이너가 실제로 떠 있는지 `docker-compose ps` 로 확인

- **문제:** 패널에 No data 메시지 표시
  - **해결:**
    - Prometheus 쿼리 입력창에서 먼저 쿼리가 정상 동작하는지 테스트해 보도록 안내

#### 체크포인트 (Phase 4 끝)

- 각 학생이 2~3개 이상의 패널이 있는 대시보드를 저장했는지 확인
- RPS와 에러율 패널이 정상적으로 값 변화를 보여주는지 확인

---

### 3-5. Phase 5: 통합 테스트 및 토론

#### 핵심 의도

- 랜덤/인위적인 트래픽을 만들어 **모니터링이 왜 필요한지 몸으로 느끼게** 하는 것

#### 교사 설명 포인트

1. 정상 트래픽 vs 에러 트래픽 스크립트의 차이 설명
2. 트래픽 증가에 따라
   - Prometheus의 그래프 변화
   - Grafana 대시보드 변화
   를 직접 비교해 보게 함

3. 간단한 시나리오 토론 유도:
   - "에러율이 20%까지 올라가면 어떤 조치를 해야 할까?"
   - "응답 시간이 길어지면 어디를 먼저 의심해야 할까? (DB? 네트워크? 애플리케이션?)"

#### 체크포인트 (Phase 5 끝)

- 학생이 자신의 대시보드를 화면 공유하거나 스크린샷으로 보여 줄 수 있는지
- 최소 1개 이상 인사이트(예: "에러 트래픽 넣으니까 에러율 게이지가 빨간색으로 바뀌었다")를 말로 설명할 수 있는지

---

## 4. 평가 및 과제 아이디어

### 4-1. 간단한 평가 루브릭

| 항목 | 기준 |
|------|------|
| 환경 구성 | Docker Compose, Kubernetes 리소스를 문제 없이 생성/삭제할 수 있는가 |
| 메트릭 이해 | 최소 2개 이상의 메트릭 의미를 설명할 수 있는가 |
| 대시보드 구성 | 실시간 변화를 확인할 수 있는 패널을 2개 이상 구성했는가 |
| 문제 해결 | 기본적인 에러 상황에서 로그/describe를 통해 원인을 찾으려 시도하는가 |

### 4-2. 확장 과제 (과제 또는 보충용)

- Flask 앱에 **추가 메트릭**을 넣어보게 하기
  - 예: 특정 엔드포인트 호출 횟수, 사용자 ID별 카운트 등
- Grafana에서 **템플릿 변수(Variables)** 를 사용해 엔드포인트 선택 필터 추가하기
- Kubernetes Horizontal Pod Autoscaler(HPA)를 붙이고, CPU 사용량에 따라 파드 수가 늘어나는 것까지 모니터링하게 하기

---

## 5. 예상 트러블슈팅 모음

### 5-1. Minikube 관련 공통 이슈

- **Minikube가 아예 안 뜨는 경우**
  - 메모리 부족 가능성 → `minikube start --memory=4096 --cpus=2`
  - Docker 데몬 문제 → `sudo systemctl restart docker`

- **클러스터가 꼬인 느낌이 들 때**
  ```bash
  minikube delete --all --purge
  minikube start --memory=4096 --cpus=2
  ```

### 5-2. YAML 오타 이슈

- `imagePullPolicy: Never` 오타, 들여쓰기 문제 등으로 배포 실패
  - 해결: VS Code의 YAML 플러그인 사용 권장, 또는 `kubectl apply -f ...` 에러 메시지 함께 읽기

### 5-3. 네트워크/포트 이슈

- 포트포워딩 후에도 접속이 안 되는 경우
  - 로컬 포트(5000, 9090, 3000)에 이미 다른 프로세스가 바인딩되어 있는지 확인
  - 또는, 학생이 WSL/VM 혼용 환경에서 실습하는 경우, 접근 주소(`localhost` vs `VM IP`)가 다를 수 있음 설명

---

## 6. 수업 마무리 멘트 예시

- "오늘은 **애플리케이션 → 컨테이너 → Kubernetes → 모니터링** 으로 이어지는 전체 흐름을 한 번에 경험했습니다."
- "실제 현업에서는 여기에 **CI/CD, 로그 수집(ELK/EFK), 알림 시스템(Slack/메일)** 이 더해져서 하나의 플랫폼을 이룹니다."
- "이번 실습이, 여러분이 나중에 DevOps나 SRE, 클라우드 엔지니어 역할을 할 때 기본기가 되길 바랍니다."

---

## 7. 파일 구조 및 배포 관련 메모 (교사용)

- 학생에게 배포할 파일:
  - `week14-lab-student.md` (실습 가이드)
- 교사용 파일:
  - 본 문서 `week14-lab-teacher.md` (해설, 트러블슈팅, 평가 기준)

- 미리 준비해 두면 좋은 것:
  - Docker 이미지를 미리 빌드해 놓은 상태의 VM 스냅샷
  - 네트워크 막힌 환경(프록시 등)을 대비한 오프라인 패키지 플랜

---

**참고:** 수업 중 실시간으로 YAML/코드를 다 타이핑하기보다는, 
학생들에게는 최소한의 수정만 하게 하고 **핵심 개념 설명과 관찰(모니터링) 경험**에 시간을 더 쓰는 것이 추천됩니다.
