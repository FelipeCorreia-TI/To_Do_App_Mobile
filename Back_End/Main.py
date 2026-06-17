from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://Felipe:10072008FCSj#@127.0.0.1:3306/to_do_list'

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

class Tarefa (db.Model):
    id= db.Column(db.Integer,primary_key=True)
    tarefa = db.Column(db.String(100))
    descricao = db.Column(db.Text)
    checado = db.Column(db.String(1),default='N') 


@app.route('/tarefas', methods=['GET'])
def lista():
    tarefas = Tarefa.query.all()
    lista =[{"id":t.id,"tarefa":t.tarefa,"descricao":t.descricao,"checado":t.checado} for t in tarefas]
    return jsonify(lista)

@app.route('/tarefas',methods=['POST'])
def criar():
    dados = request.get_json()
    nova = Tarefa(tarefa=dados['tarefa'],descricao=dados['descricao'])
    return jsonify({"status":"Sucesso"}),201

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')