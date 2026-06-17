import urllib.parse
from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

senha_original ='10072008FCSj#@'

senha_aceita= urllib.parse.quote_plus(senha_original)

app.config['SQLALCHEMY_DATABASE_URI'] = f'mysql+pymysql://Felipe:{senha_aceita}@127.0.0.1:3306/to_do_list'

app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)

class Tarefas (db.Model):
    id= db.Column(db.Integer,primary_key=True)
    tarefa = db.Column(db.String(100))
    descricao = db.Column(db.Text)
    checado = db.Column(db.String(1),default='N') 


@app.route('/tarefas', methods=['GET'])
def lista():
    tarefas = Tarefas.query.all()
    lista =[{"id":t.id,"tarefa":t.tarefa,"descricao":t.descricao,"checado":t.checado} for t in tarefas]
    return jsonify(lista)

@app.route('/tarefas',methods=['POST'])
def criar():
    dados = request.get_json()
    nova = Tarefas(tarefa_post=dados['tarefa'],descricao_post=dados['descricao'])
    db.session.add(nova)
    db.session.commit()
    return jsonify({"status":"Sucesso"}),201

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')