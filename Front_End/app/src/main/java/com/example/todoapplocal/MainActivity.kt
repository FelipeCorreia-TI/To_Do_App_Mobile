package com.example.todoapplocal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextDecoration
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

data class Tarefa (
    @SerializedName("id") val id: Int = 0,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("concluido") val concluida: Boolean = false
)

interface ApiService{
    @GET("/tarefas")
    suspend fun buscarTarefas(): List<Tarefa>

    @POST("/tarefas")
    suspend fun criarTarefa(@Body tarefa:Tarefa):
            Response<Void>

    @PUT("/tarefas/{id}")
    suspend fun atualizarTarefa(@Path("id") id:Int,@Body tarefa:Tarefa): Response<Void>

    @DELETE("/tarefas/{id}")
    suspend fun deletarTarefa(@Path("id") id:Int):Response<Void>
}

object RetrofitClient{
    private const val BASE_URL = "http://10.0.2.2:5000"

    val api: ApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val listaDeTarefas = remember {mutableStateListOf<Tarefa>()}

    var textoDigitado by remember { mutableStateOf(" ")}
    var descricaoDigitada by remember {mutableStateOf(" ")}


    var campoTitulo by remember {mutableStateOf("")}
    var campoDescricao by remember {mutableStateOf("")}
    var carregando by remember {mutableStateOf(false)}


    fun atualizarListaDoServidor(){
        coroutineScope.launch{
            carregando = true
            try{
                val tarefasDoBanco =
                    RetrofitClient.api.buscarTarefas()
                    listaDeTarefas.clear()

                listaDeTarefas.addAll(tarefasDoBanco)
            } catch (e:Exception){
                Toast.makeText(context,"Erro ao buscar dados: ${e.localizedMessage}",Toast.LENGTH_LONG).show()
            } finally {
                carregando = false
            }
        }
    }

    LaunchedEffect(Unit) {
        atualizarListaDoServidor()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text(text = "Meu To-Do List Full-Stack", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()){
            OutlinedTextField(value= campoTitulo,
                onValueChange = {campoTitulo=it},
                label = {Text("Título da tarefa...")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                enabled= !carregando,
                onClick= {
                    if (campoTitulo.isNotBlank()){
                        coroutineScope.launch {
                            try{
                                val novaTarefa = Tarefa(titulo = campoTitulo, descricao = campoDescricao)

                                RetrofitClient.api.criarTarefa(novaTarefa)
                                campoTitulo = ""
                                campoDescricao = ""

                                atualizarListaDoServidor()
                            } catch (e: Exception) {
                                Toast.makeText(context,"Erro ao salvar",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                Text("Add")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if(carregando) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(listaDeTarefas.size){
                index -> val itemTarefa = listaDeTarefas[index]

                ItemTarefa(
                    tarefa = itemTarefa,
                    onStatusItemChange= {
                        coroutineScope.launch {
                            try {
                                val tarefaAtualizada = itemTarefa.copy(concluida = !itemTarefa.concluida)

                                RetrofitClient.api.atualizarTarefa(itemTarefa.id,tarefaAtualizada)
                                atualizarListaDoServidor()
                            } catch (e: Exception){
                                Toast.makeText(context, "Erro ao atualizar status",  Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onDeletarClique = {
                        coroutineScope.launch {
                            try {
                                RetrofitClient.api.deletarTarefa(itemTarefa.id)
                                atualizarListaDoServidor()
                            } catch (e: Exception){
                                Toast.makeText(context,"Erro ao deletar",Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onSalvarEdicao= { novoTitulo, novaDescricao ->
                        coroutineScope.launch {
                            try {
                                val tarefaEditada =
                                    itemTarefa.copy(titulo = novoTitulo, descricao = novaDescricao)

                                RetrofitClient.api.atualizarTarefa(itemTarefa.id, tarefaEditada)

                                atualizarListaDoServidor()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao salvar edição", Toast.LENGTH_SHORT)
                                    .show()
                              }
                         }
                    }
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
    onSalvarEdicao: (String, String) -> Unit
) {
    var expandido by remember {mutableStateOf(false)}
    var emEdicao by remember {mutableStateOf(false)}

    var textoTituloEdicao by remember {mutableStateOf(tarefa.titulo)}
    var textoDescricaoEdicao by remember {mutableStateOf(tarefa.descricao)}


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { expandido = !expandido }
    ) {
        Column(modifier=Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tarefa.concluida,
                    onCheckedChange =  { _ -> onStatusItemChange() }
                )

                if(emEdicao){
                    OutlinedTextField(
                        value = textoTituloEdicao,
                        onValueChange = {textoTituloEdicao=it},
                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp),
                        label = {Text("Editar Título")}
                    )
                } else{
                Text(
                    text = tarefa.titulo,
                    modifier= Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    textDecoration = if(tarefa.concluida) TextDecoration.LineThrough else TextDecoration.None
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