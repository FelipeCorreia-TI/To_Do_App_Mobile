package com.example.todoapplocal

import android.R
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapplocal.ui.theme.ToDoAppLocalTheme
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf


data class Tarefa (
    val id: Int,
    val titulo: String,
    val descricao: String,
    var isConcluida: Boolean = false
)

val listadetarefa = remember { mutableStateListOf<Tarefa>() }
var proximoId = 1

fun adicionarTarefa(texto:String,textoSecundario:String) {
    if(texto.isNotBlank()){
        val nova = Tarefa(id = proximoId, titulo = texto, descricao = textoSecundario)
        listadetarefa.add(nova)
        proximoId++
    }
}

fun

class MainActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            TelaPrincipalTodo()
        }

    }
}

@Composable
fun TelaPrincipalTodo() {

    Column(
        modifier =
            Modifier.
            fillMaxSize().
            padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value= textoTarefa,
                onValueChange = { novoTexto ->
                    textoTarefa = novoTexto
                },
                placeholder = {Text("Digite  uma nova tarefa...")},
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))


        }

    }
}