from flask import Flask, request, jsonify
from flask_cors import CORS
import mysql.connector

app = Flask(__name__) #Gera o serividor web
CORS(app) #Permite estar na mesma porta que o Recat :5000

def conectar_db(): #Função que tenta conectar a database
    return mysql.connector.connect(
        host ="localhost",
        user= "Felipe",
        port="3306",
        password="10072008FCSj#@",
        database="to_do_list"
    ) #Campo que pode variar de acordo com a config do db local.

@app.route('/tarefas', methods=['POST'])
def criar_tarefas():
    dados = request.get_json() #lê e armazena o JSON recebido do front
    titulo= dados.get('tarefa') #através de um GET lê a coluna tarefa
    descricao= dados.get('descricao') #através de um GET lê a coluna descricao

    conexao= conectar_db() 
    cursor= conexao.cursor() #Através da conexão com o banco permite executar comandos SQL

    sql = "INSERT INTO tarefas (tarefa,descricao) VALUES (%s, %s)"
    valores = (titulo, descricao)

    cursor.execute(sql, valores) #Executa a string SQL com os valores que serão substituidos em %s
    conexao.commit() #Commita

    id_criado = cursor.lastrowid #Pega o id do auto_increment

    cursor.close() #Fecha a conexão para não gastar recursos desnecessários.
    conexao.close() #Fecha a conexão para não gastar recursos desnecessários.

    return jsonify({"id": id_criado, "tarefa": titulo,"descricao":descricao}), 201 #Retorna o valor em JSON

@app.route('/tarefas', methods=['GET'])
def listar_tarefas():
    conexao = conectar_db()
    cursor = conexao.cursor(dictionary=True) #Transfoma o valor que será recebido em um dicionário/JSON

    cursor.execute("SELECT * FROM tarefas")
    lista_tarefas = cursor.fetchall() #Capta todos os registros

    cursor.close()
    conexao.close()

    return jsonify(lista_tarefas), 200 #Devolve a lista com todos os registros

# [U]PDATE - Atualizar uma tarefa existente
@app.route('/tarefas/<int:id_tarefa>', methods=['PUT'])
def atualizar_tarefa(id_tarefa):
    dados = request.get_json()
    titulo = dados.get('tarefa')
    descricao = dados.get('descricao')

    conexao = conectar_db()
    cursor = conexao.cursor()
    
    sql = "UPDATE tarefas SET tarefa = %s, descricao = %s WHERE id = %s"
    valores = (titulo, descricao, id_tarefa)
    
    cursor.execute(sql, valores)
    conexao.commit()
    
    cursor.close()
    conexao.close()
    
    return jsonify({"mensagem": "Tarefa atualizada com sucesso!"}), 200


# [D]ELETE - Excluir tarefa
@app.route('/tarefas/<int:id_tarefa>', methods=['DELETE'])
def deletar_tarefa(id_tarefa):
    conexao = conectar_db()
    cursor = conexao.cursor()
    
    sql = "DELETE FROM tarefas WHERE id = %s"
    cursor.execute(sql, (id_tarefa,))
    conexao.commit()
    
    cursor.close()
    conexao.close()
    
    return jsonify({"mensagem": "Tarefa deletada com sucesso!"}), 200


if __name__ == '__main__':
    app.run(debug=True, port=5000) # Roda o servidor na porta 5000