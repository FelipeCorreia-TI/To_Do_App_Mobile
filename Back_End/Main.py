import urllib.parse
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy #Optamos por utilizar um ORM que facilita muito a criação de rotas
from werkzeug.security import generate_password_hash, check_password_hash

app = Flask(__name__) #Inicia o APP/SERVER

senha_original ='10072008FCSj#@' #senha_original ='@Mello2026'

senha_aceita= urllib.parse.quote_plus(senha_original) #Embaralhamos a senha e tornamo-a compreensível para o sistema já que a anterior havia # e @

app.config['SQLALCHEMY_DATABASE_URI'] = f'mysql+pymysql://Felipe:{senha_aceita}@localhost:3306/to_do_list' #Conexão database #Esse campo deve mudar de acordo com a configuração da database local mysql #f'mysql+pymysql://root:{senha_aceita}@localhost:3306/to_do_list'

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False #Boa prática que ajuda a liberar recurso desncessário durante as requisições do banco
db = SQLAlchemy(app)

class usuarios(db.Model):
    id= db.Column(db.Integer,primary_key=True)
    email = db.Column(db.String(100), unique = True,nullable=False)
    senha = db.Column(db.String(255),nullable=False)

class tarefas (db.Model): #Definição da tabela Tarefas no Server Side para garantir a conformidade dos dados
    id= db.Column(db.Integer,primary_key=True)
    titulo = db.Column(db.String(100),nullable=False)
    descricao = db.Column(db.Text)
    concluido = db.Column(db.Boolean,default = False) 
    usuario_id = db.Column(db.Integer,db.ForeignKey('usuarios.id'),nullable=False)

@app.route('/cadastro',methods=['POST'])
def cadastro():
    dados= request.get_json()
    if not dados or 'email' not in dados or 'senha' not in dados:
        return jsonify({"erro":"Dados incompletos"}),400
    
    if usuarios.query.filter_by(email=dados['email']).first():
        return jsonify({"erro":"Este e-mail já está cadastrado"}),400
    
    senha_cripto = generate_password_hash(dados['senha'])
    novo_usuario = usuarios(email=dados['email'],senha=senha_cripto)

    db.session.add(novo_usuario)
    db.session.commit()
    return jsonify({"status":"Usuário criado com sucesso"}),201

@app.route('/login',methods=['POST'])
def login():
    dados = request.get_json()
    usuario = usuarios.query.filter_by(email=dados.get('email')).first()

    if usuario and check_password_hash(usuario.senha,dados.get('senha')):
        return jsonify({
            "status":"Sucesso",
            "usuario_id":usuario.id,
            "email":usuario.email
        }),200
    
    return jsonify ({"erro":"E-mail ou senha incorretas"}),401
@app.route('/tarefas', methods=['GET'])  #Rota GET - [Pegar dados]
def lista():
    user_id = request.args.get("usuario_id")

    if not user_id:
        return jsonify({"erro":"ID do usuário não fornecido"}),400

    lista_tarefa = tarefas.query.filter_by(usuario_id=user_id).all()

     #ORM facilitando com uma query 'automática'
    lista =[
        {"id":t.id,
         "titulo":t.titulo,
         "descricao":t.descricao,
         "concluido":bool(t.concluido)} 
        for t in lista_tarefa] #Percorre todos as colunas da tabela e atribui cada uma a um registro único 
    return jsonify(lista) #Torna-se JSON para facilitar a comunicação Server <-> Back <-> Front

@app.route('/tarefas',methods=['POST']) #Rota POST - [Para 'postar' os novos registros]
def criar():

    dados = request.get_json(silent=True) 
     # Se 'dados' for nulo ou não for um dicionário, retorna um aviso amigável

    user_id= dados['usuario_id']
    if not dados or not isinstance(dados, dict):
        return jsonify({"erro": "O corpo da requisição precisa ser um JSON válido e conter o cabeçalho 'Content-Type: application/json'"}), 400
    
    dados = request.get_json() #Requisita o JSON para ter a estrutura e colocar os novos registros nas colunas sem que haja incoerências
    nova = tarefas(titulo=dados['titulo'],descricao=dados.get('descricao',''),concluido=dados.get('concluido',False),usuario_id= user_id) #Variável que através da DataClass lá de cima adiciona os novos registros dentro de variáveis que irão adicionar algum registro.
    db.session.add(nova) #faz o INSERT
    db.session.commit() #Commita a alteração
    return jsonify({"status":"Sucesso"}),201 #Retornará um status positivo, significa que foram adicionados | 200 - bateu e voltou as respostas | 201 - bateu e adicionou | 4xx - Erro |

@app.route('/tarefas/<int:id>', methods=['PUT'])
def atualizar(id):
    dados = request.get_json()
    tarefa= tarefas.query.get(id)

    if not tarefa:
        return jsonify({"erro": "Tarefa não encontrada"}), 404

    tarefa.titulo = dados.get('titulo', tarefa.titulo)
    tarefa.descricao = dados.get('descricao', tarefa.descricao)
    tarefa.concluido = dados.get('concluido',tarefa.concluido)

    db.session.commit()
    return jsonify({"status":"atualizado"})

@app.route('/tarefas/<int:id>',methods=['DELETE'])
def deletar(id):
    tarefa = tarefas.query.get(id)
    db.session.delete(tarefa)
    db.session.commit()
    return jsonify({"status":"Removido"})

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0', port=5000)