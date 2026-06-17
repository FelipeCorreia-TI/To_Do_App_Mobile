import urllib.parse
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy #Optamos por utilizar um ORM que facilita muito a criação de rotas

app = Flask(__name__) #Inicia o APP/SERVER

senha_original ='10072008FCSj#@'

senha_aceita= urllib.parse.quote_plus(senha_original) #Embaralhamos a senha e tornamo-a compreensível para o sistema já que a anterior havia # e @

app.config['SQLALCHEMY_DATABASE_URI'] = f'mysql+pymysql://Felipe:{senha_aceita}@127.0.0.1:3306/to_do_list' #Conexão database

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False #Boa prática que ajuda a liberar recurso desncessário durante as requisições do banco
db = SQLAlchemy(app)

class Tarefas (db.Model): #Definição da tabela Tarefas no Server Side para garantir a conformidade dos dados
    id= db.Column(db.Integer,primary_key=True)
    tarefa = db.Column(db.String(100))
    descricao = db.Column(db.Text)
    checado = db.Column(db.String(1),default='N') 


@app.route('/tarefas', methods=['GET'])  #Rota GET - [Pegar dados]
def lista():
    tarefas = Tarefas.query.all() #ORM facilitando com uma query 'automática'
    lista =[{"id":t.id,"tarefa":t.tarefa,"descricao":t.descricao,"checado":t.checado} for t in tarefas] #Percorre todos as colunas da tabela e atribui cada uma a um registro único 
    return jsonify(lista) #Torna-se JSON para facilitar a comunicação Server <-> Back <-> Front

@app.route('/tarefas',methods=['POST']) #Rota POST - [Para 'postar' os novos registros]
def criar():
    dados = request.get_json() #Requisita o JSON para ter a estrutura e colocar os novos registros nas colunas sem que haja incoerências
    nova = Tarefas(tarefa_post=dados['tarefa'],descricao_post=dados['descricao']) #Variável que através da DataClass lá de cima adiciona os novos registros dentro de variáveis que irão adicionar algum registro.
    db.session.add(nova) #faz o INSERT
    db.session.commit() #Commita a alteração
    return jsonify({"status":"Sucesso"}),201 #Retornará um status positivo, significa que foram adicionados | 200 - bateu e voltou as respostas | 201 - bateu e adicionou | 4xx - Erro |

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')