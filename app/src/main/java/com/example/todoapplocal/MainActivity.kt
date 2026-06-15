package com.example.todoapplocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


data class Tarefa (
    val id: Int,
    val titulo: String,
    val descricao: String,
    var isConcluida: Boolean = false
)


val listadetarefa =  mutableStateListOf<Tarefa>()
var proximoId = 1

fun adicionarTarefa(texto:String,textoSecundario:String) {
    if(texto.isNotBlank()){
        val nova = Tarefa(id = proximoId, titulo = texto, descricao = textoSecundario)
        listadetarefa.add(nova)
        proximoId++
    }
}
//Na função UPDATE atual, vai faltar a principal função de conseguir alterar as tarefas escritas.
fun alternarStatusTarefa(id:Int){
    val index = listadetarefa.indexOfFirst { it.id==id }
    if (index != -1){
        val tarefaAtual = listadetarefa[index]

        listadetarefa[index] = tarefaAtual.copy(isConcluida = !tarefaAtual.isConcluida)
    }
}

fun deletarTarefa(id: Int){
    listadetarefa.removeIf { it.id == id  }
}

class MainActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ){
                TeladotodoApp()
            }

        }

    }
}

@Composable

fun TeladotodoApp(){
    var textoDigitado by remember { mutableStateOf(" ")}
    var descricaoDigitada by remember {mutableStateOf(" ")}

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ){
        Text(text = "Meu To-Do List Local", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
            OutlinedTextField(
                value = textoDigitado,
                onValueChange = { textoDigitado = it},
                label = { Text("Nova tarefa...")},
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = descricaoDigitada,
                onValueChange = { descricaoDigitada = it},
                label = { Text("Descreva a tarefa...")},
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                adicionarTarefa(textoDigitado,descricaoDigitada)
                textoDigitado = ""
                descricaoDigitada = ""
            }) {
                Text("Add")
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(listadetarefa.size){
                    index -> val tarefa = listadetarefa[index]

                ItemTarefa(
                    tarefa = tarefa,
                    onStatusItemChange = {
                        alternarStatusTarefa(tarefa.id)},
                    onDeletarClique = {deletarTarefa(tarefa.id)}
                )
            }
        }
    }
}




@Composable
fun ItemTarefa(
    tarefa: Tarefa,
    onStatusItemChange: () -> Unit,
    onDeletarClique:() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarefa.isConcluida,
                        onCheckedChange =  { _ -> onStatusItemChange() }
            )

            Text(
                text = tarefa.descricao,
                modifier= Modifier.weight(1f).padding(start = 8.dp)
            )

            Button(onClick = onDeletarClique) {
                Text("Excluir")
            }
        }
    }
}