package com.kotlinbasics

import android.os.Bundle
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
    println("=== Kotlin Classes ===")

    // 기본 클래스
    class Student {
        var name: String = ""
        var age: Int = 0

        fun introduce() {
            println("Hi, I'm $name and I'm $age years old")
        }
    }

    val student = Student()
    student.name = "Alice"
    student.age = 20
    student.introduce()

    // 데이터 클래스
    data class Person(val name: String, val age: Int)

    val person1 = Person("Bob", 25)
    val person2 = Person("Bob", 25)

    println("Person1: $person1")
    println("Equal? ${person1 == person2}")
}

private fun week03Collections(){
    println("=== Kotlin Collections ===")

    val fruits = listOf("apple", "banana", "orange")
    val mutableFruits = mutableListOf("apple", "banana")

    println("Fruits: $fruits")
    mutableFruits.add("grape")
    println("Mutable fruits: $mutableFruits")

// 맵
    val scores = mapOf("Alice" to 95, "Bob" to 87, "Charlie" to 92)
    println("Scores: $scores")

// 반복문
    for (fruit in fruits) {
        println("I like $fruit")
    }

    scores.forEach { (name, score) ->
        println("$name scored $score")
    }
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