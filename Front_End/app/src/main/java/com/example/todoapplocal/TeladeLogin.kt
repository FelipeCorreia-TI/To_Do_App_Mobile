package com.example.todoapplocal

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun TelaLogin(onLoginSucesso:() -> Unit ){
    var email by remember { mutableStateOf("") }
    var senha by remember {mutableStateOf("")}
    var erroMensagem by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var carregando by remember { mutableStateOf(false)}

    Column(
        modifier= Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text= "Bem-vindo ao seu TO-DO-LIST APP", style = MaterialTheme.typography.headlineLarge)

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

        Button(enabled =!carregando,
            onClick = {
            if(email.isNotBlank() && senha.isNotBlank()){
                coroutineScope.launch {
                    carregando = true
                    erroMensagem = ""
                    try {
                        val resposta =
                            RetrofitClient.api.efetuarLogin(LoginRequest(email,senha))
                        if(resposta.isSuccessful && resposta.body() != null){
                            RetrofitClient.usuarioLogadoId=
                                resposta.body()!!.usuarioId
                                Toast.makeText(context,"Bem-Vindo!", Toast.LENGTH_SHORT).show()
                                onLoginSucesso()
                        } else{
                            erroMensagem = "E-mail ou Senha não cadastrado!"
                        }
                    } catch (e: Exception){
                        erroMensagem = "Erro de conexão com o servidor."
                    } finally {
                        carregando = false
                    }
                }
            } else {
                erroMensagem = "Preencha todos os campos!"
            }
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            enabled = !carregando,
            onClick = {
                if (email.isNotBlank() && senha.isNotBlank()){
                    if(senha.length < 6){
                        erroMensagem = "A senha deve ter no mínimo 6 caracteres!"
                        return@OutlinedButton
                    }
                    coroutineScope.launch {
                        carregando = true
                        erroMensagem = ""
                        try{
                            val resposta = RetrofitClient.api.cadastrarUsuario(LoginRequest(email,senha))
                            if(resposta.isSuccessful){
                                Toast.makeText(context,"Conta criada! Clique em Entrar.", Toast.LENGTH_LONG).show()
                                senha = ""
                            }else{
                                erroMensagem = "Este e-mail já está cadastrado."
                            }
                        } catch (e: Exception){
                            erroMensagem = "Erro ao tentar cadastrar."
                        } finally {
                            carregando = false
                        }
                    }
                } else {
                    erroMensagem = "Preencha e-mail e senha para cadastrar !" }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Nova conta")
        }
    }
}