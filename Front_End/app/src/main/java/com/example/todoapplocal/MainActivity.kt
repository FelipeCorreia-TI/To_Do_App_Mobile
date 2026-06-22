package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapplocal.TelaLogin
import com.example.todoapplocal.TeladotodoApp

class MainActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController= navController,
                    startDestination = "login"
                ){
                    composable("login"){
                        TelaLogin(onLoginSucesso ={
                            navController.navigate("tarefas"){
                                popUpTo("login"){
                                    inclusive=true
                                }
                            }
                        } )
                    }

                    composable("tarefas"){
                        TeladotodoApp()
                    }
                }
            }
        }
    }
}