package com.example.todoapplocal

import android.graphics.Outline
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TelaLogin(onLoginSucesso:() -> Unit ){
    var email by remember { mutableStateOf("") }
    var senha by remember {mutableStateOf("")}
    var erroMensagem by remember { mutableStateOf("") }

    Column(
        modifier= Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text= "Bem-vindo ao seu TO-DO-LIST", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier=Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {email=it},
            label = {Text("E-mail")},
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = {senha=it},
            label = {Text("Senha")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if(erroMensagem.isNotBlank()){
            Spacer(modifier = Modifier.height(8.dp))
            Text(text=erroMensagem, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick ={
            if(email == "felipe@teste.com" && senha == "123456"){
                onLoginSucesso()
            }else {
                erroMensagem = "E-mail ou senha incorretos!"
            }
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
    }
}