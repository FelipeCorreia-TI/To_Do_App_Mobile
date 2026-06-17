package com.example.todoapplocal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration


data class Tarefa (
    val id: Int,
    val titulo: String,
    val descricao: String,
    var isConcluida: Boolean = false
)


val listadetarefa =  mutableStateListOf<Tarefa>()
var proximoId = 1

//Create Local, Pega os 2 valores, coloca em uma variável e adiciona para a lista, gerando automaticamente um novo ID a cada CREATE
fun adicionarTarefa(texto:String,textoSecundario:String) {
    if(texto.isNotBlank()){
        val nova = Tarefa(id = proximoId, titulo = texto, descricao = textoSecundario)
        listadetarefa.add(nova)
        proximoId++
    }
}


//Função do Check para as tarefas, ele garante que esteja selecionando o id do item que o botão foi checado.
fun alternarStatusTarefa(id:Int){
    val index = listadetarefa.indexOfFirst { it.id==id }
    if (index != -1){
        val tarefaAtual = listadetarefa[index]

        listadetarefa[index] = tarefaAtual.copy(isConcluida = !tarefaAtual.isConcluida)
    }
}

//Update
fun editarConteudoTarefa(id: Int,novoTitulo:String,novaDescricao: String){
    val index = listadetarefa.indexOfFirst { it.id == id }
    if (index != -1) {
        val tarefaAtual= listadetarefa[index]
        listadetarefa[index]= tarefaAtual.copy(titulo = novoTitulo, descricao = novaDescricao)
    }
}

//DELETE de tarefas
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
                label = { Text("Descreva a tarefa(opcional)...")},
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
                    index -> val Listatarefa = listadetarefa[index]

                ItemTarefa(
                    tarefa = Listatarefa,
                    onStatusItemChange = { alternarStatusTarefa(Listatarefa.id)},
                    onDeletarClique = {deletarTarefa(Listatarefa.id)},
                    onSalvarEdicao = {novoT,novaD -> editarConteudoTarefa(Listatarefa.id,novoT,novaD)}
                )
            }
        }
    }
}




@Composable
fun ItemTarefa(
    tarefa: Tarefa,
    onStatusItemChange: () -> Unit,
    onDeletarClique:() -> Unit,
    onSalvarEdicao: (String,String) -> Unit
) {
    var expandido by remember {mutableStateOf(false)}
    var emEdicao by remember {mutableStateOf(false)}

    var textoTituloEdicao by remember {mutableStateOf(tarefa.titulo)}
    var textoDescricaoEdicao by remember {mutableStateOf(tarefa.descricao)}

    LaunchedEffect(tarefa) {
        textoTituloEdicao= tarefa.titulo
        textoDescricaoEdicao = tarefa.descricao
    }
    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable{expandido=!expandido}
    ) {
        Column(modifier=Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tarefa.isConcluida,
                    onCheckedChange =  { _ -> onStatusItemChange() }
                )

                if(emEdicao){
                    OutlinedTextField(
                        value = textoTituloEdicao,
                        onValueChange = {textoTituloEdicao=it},
                        modifier =
                            Modifier.weight(1f).padding(horizontal = 8.dp),
                        label = {Text("Editar Título")}
                    )
                } else{
                Text(
                    text = tarefa.titulo,
                    modifier= Modifier.weight(1f).padding(start = 8.dp),
                    textDecoration = if(tarefa.isConcluida) TextDecoration.LineThrough else TextDecoration.None
                )
            }

                Button(onClick = onDeletarClique, colors = ButtonDefaults.buttonColors(containerColor= MaterialTheme.colorScheme.error)) {
                    Text("Excluir")
                }
            }
            if (expandido){
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color=MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier=Modifier.height(12.dp))
                if(emEdicao){
                    OutlinedTextField(
                        value = textoDescricaoEdicao,
                        onValueChange = {textoDescricaoEdicao=it},
                        modifier = Modifier.fillMaxWidth(),
                        label = {Text("Editar Descrição")}
                    )
                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onSalvarEdicao(textoTituloEdicao,textoDescricaoEdicao)
                            emEdicao = false
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Salvar")
                    }
                }else {
                    val textoExibido = if(tarefa.descricao.isNotBlank())
                        tarefa.descricao else "Descrição: (Vazio)"
                    Text(
                        text = textoExibido,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.align(Alignment.End))

                    Button(
                        onClick = {emEdicao=true},
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Editar")
                    }
                }
            }
        }

    }
}