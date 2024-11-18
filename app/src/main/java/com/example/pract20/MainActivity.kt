package com.example.pract20

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager //sensorManager будет доступна только внутри класса, в котором она объявлена. lateinit-Это означает, что переменная будет инициализирована позже, не обязательно в момент объявления
    private var gyroSensor: Sensor? = null  //Sensor?: Это указание типа переменной. Sensor — это класс из Android SDK, представляющий датчик. ? после Sensor означает, что переменная может быть null

    private lateinit var gyroXTextView: TextView
    private lateinit var gyroYTextView: TextView
    private lateinit var gyroZTextView: TextView

    private lateinit var startButton: Button // Кнопка для запуска/остановки гироскопа

    private var isGyroActive = false // Переменная для отслеживания состояния гироскопа

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация TextView для отображения значений гироскопа
        gyroXTextView = findViewById(R.id.gyroX)
        gyroYTextView = findViewById(R.id.gyroY)
        gyroZTextView = findViewById(R.id.gyroZ)

        // Инициализация кнопки
        startButton = findViewById(R.id.startButton)

        // Установка обработчика нажатия на кнопку
        startButton.setOnClickListener {
            if (isGyroActive) { //Это булева переменная, отслеживающая состояние гироскопа (включен/выключен).
                stopGyro()
            } else {
                startGyro() // Это функция, которая запускает
            }
        }

        // Инициализация SensorManager и гироскопа
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager // getSystemService(): Это метод объекта Context, позволяющий получить доступ к различным системным службам.
        // Context.SENSOR_SERVICE: Это константа (строка), которая идентифицирует службу управления датчиками
        // Метод getSystemService() возвращает объект типа IBinder (обобщенный интерфейс для взаимодействия со службами). Но вам нужен объект SensorManager для работы с датчиками. as SensorManager преобразует объект IBinder в объект SensorManager
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    override fun onResume() {
        super.onResume()
        // Регистрация слушателя для гироскопа, если он активен
        if (isGyroActive) {  //сли переменная isGyroActive имеет значение true, то есть гироскоп активен.
            //  gyroSensor?.also { ... }: Это безопасный вызов
            gyroSensor?.also {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) //SENSOR_DELAY_NORMAL — это средняя частота обновления.
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Отмена регистрации слушателя при приостановке активности
        if (isGyroActive) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            val gyroX = event.values[0]
            val gyroY = event.values[1]
            val gyroZ = event.values[2]

            // Обновление текстовых полей с новыми значениями гироскопа
            gyroXTextView.text = "Gyro X: %.2f".format(gyroX) //это спецификатор формата, который означает: вывести число с плавающей запятой (f)
            gyroYTextView.text = "Gyro Y: %.2f".format(gyroY)
            gyroZTextView.text = "Gyro Z: %.2f".format(gyroZ)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Можно оставить пустым, если не требуется обработка изменений точности
    }

    private fun startGyro() {
        isGyroActive = true // Устанавливаем состояние гироскопа как активное

        // Регистрация слушателя для гироскопа
        gyroSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        startButton.text = "Stop Gyroscope" // Изменение текста кнопки
    }

    private fun stopGyro() {
        isGyroActive = false // Устанавливаем состояние гироскопа как неактивное

        // Отмена регистрации слушателя гироскопа
        sensorManager.unregisterListener(this)

        startButton.text = "Start Gyroscope" // Изменение текста кнопки обратно
    }
}