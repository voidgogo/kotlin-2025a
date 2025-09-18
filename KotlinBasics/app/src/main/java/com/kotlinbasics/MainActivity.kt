package com.kotlinbasics

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kotlinbasics.ui.theme.KotlinBasicsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinBasicsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        week02Variables()

        week02Functions()

        week03Classes()

        week03Collections()
    }
}

private fun week02Variables() {
//    println("=== Week 1: Variables ===")
//
//    val courseName = "Mobile Programming"
//    var week = 1
//
//    println("Course: $courseName")
//    println("Week: $week")

// 변수 선언과 타입
    println("=== Kotlin Variables ===")

// val (불변) vs var (가변)
    val name = "Android"
    var version = 14
    println("Hello $name $version")

// 타입 명시
    val age: Int = 25
    val height: Double = 175.5
    val isStudent: Boolean = true

    println("Age: $age, Height: $height, Student: $isStudent")

// 널 허용 타입
    var nickname: String? = null
    nickname = "Andy"
    println("Nickname: ${nickname?.length}")
}

private fun week02Functions() {
//    println("=== Week 1: Functions ===")
//
//    fun greet(name: String) = "Hello, $name!"
//
//    println(greet("Android Developer"))

    // 함수 정의와 사용
    println("=== Kotlin Functions ===")

    // 기본 함수
    fun greet(name: String): String {
        return "Hello, $name!"
    }

    // 단일 표현식 함수
    fun add(a: Int, b: Int) = a + b

    // 기본 매개변수
    fun introduce(name: String, age: Int = 20) {
        println("My name is $name and I'm $age years old")
    }

// 함수 호출
    println(greet("Kotlin"))
    println("Sum: ${add(5, 3)}")
    introduce("Alice")
    introduce("Bob", 25)
}

private fun week03Classes() {
    Log.d("KotlinWeek03", "=== 3주차: 클래스 실습 시작 ===")

    // 1. 기본 클래스 정의와 인스턴스
    class Person(val name: String, var age: Int) {  // 기본 생성자
        fun introduce() {
            Log.d("KotlinWeek03", "안녕하세요, $name ($age 세)입니다.")
        }
        fun birthday() {
            age++
            Log.d("KotlinWeek03", "$name 의 생일! 이제 $age 세.")
        }
    }

    val person1 = Person("홍길동", 25)
    person1.introduce()
    person1.birthday()

    // 2. 보조 생성자 (init 블록 활용)
    open class Animal(var species: String) {
        var weight: Double = 0.0
        constructor(species: String, weight: Double) : this(species) {  // 보조 생성자
            this.weight = weight
            Log.d("KotlinWeek03", "$species 의 무게: $weight kg")
        }
        open fun makeSound() {  // 오버라이드 가능
            Log.d("KotlinWeek03", "$species 가 소리를 냅니다.")
        }
    }

    val dog = Animal("개", 10.5)
    dog.makeSound()

    // 3. 상속
    class Dog(species: String, weight: Double, val breed: String) : Animal(species, weight) {
        override fun makeSound() {
            Log.d("KotlinWeek03", "$breed($species)가 멍멍 짖습니다!")
        }
    }

    val myDog = Dog("개", 15.0, "골든 리트리버")
    myDog.makeSound()

    // 4. 데이터 클래스 (equals, hashCode, toString 자동 생성)
    data class Book(val title: String, val author: String, val pages: Int)

    val book1 = Book("코틀린 입문", "John Doe", 300)
    val book2 = Book("코틀린 입문", "John Doe", 300)
    Log.d("KotlinWeek03", "book1 == book2: ${book1 == book2}")  // true
    Log.d("KotlinWeek03", "book1: $book1")  // toString 자동 출력

    Log.d("KotlinWeek03", "=== 클래스 실습 끝 ===")
}

private fun week03Collections() {
    Log.d("KotlinWeek03", "=== 3주차: 컬렉션 실습 시작 ===")

    // 1. List (순서 있음, 중복 허용)
    val fruits = listOf("사과", "바나나", "사과")  // 불변 List
    Log.d("KotlinWeek03", "과일 목록: $fruits")
    Log.d("KotlinWeek03", "사과 개수: ${fruits.count { it == "사과" }}")  // 람다로 카운트

    val mutableFruits = mutableListOf("오렌지")
    mutableFruits.addAll(fruits)
    mutableFruits.remove("바나나")
    Log.d("KotlinWeek03", "변경된 목록: $mutableFruits")

    // 2. Set (순서 없음, 중복 불가)
    val uniqueNumbers = setOf(1, 2, 2, 3)
    Log.d("KotlinWeek03", "고유 숫자: $uniqueNumbers")  // [1, 2, 3]

    val mutableSet = mutableSetOf(4, 5)
    mutableSet.addAll(uniqueNumbers)
    Log.d("KotlinWeek03", "합친 Set: $mutableSet")

    // 3. Map (키-값 쌍)
    val scores = mapOf("Alice" to 95, "Bob" to 87, "Charlie" to 92)
    Log.d("KotlinWeek03", "성적: $scores")
    Log.d("KotlinWeek03", "Alice 점수: ${scores["Alice"]}")

    val mutableScores = scores.toMutableMap()
    mutableScores["David"] = 88
    mutableScores.remove("Bob")
    Log.d("KotlinWeek03", "변경된 성적: $mutableScores")

    // 4. 컬렉션 연산 (filter, map, forEach)
    val numbers = listOf(1, 2, 3, 4, 5)
    val evens = numbers.filter { it % 2 == 0 }  // 짝수 필터
    val squares = evens.map { it * it }  // 제곱 매핑
    Log.d("KotlinWeek03", "짝수: $evens, 제곱: $squares")

    squares.forEach { Log.d("KotlinWeek03", "제곱 값: $it") }

    // 5. 확장 함수 예시 (List 확장)
    fun <T> List<T>.printEach() {  // 확장 함수
        this.forEachIndexed { index, item -> Log.d("KotlinWeek03", "[$index]: $item") }
    }
    fruits.printEach()

    Log.d("KotlinWeek03", "=== 컬렉션 실습 끝 ===")
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinBasicsTheme {
        Greeting("Android")
    }
}