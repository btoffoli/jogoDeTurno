

String endereco = this.args.size() > 0 ? this.args.first() : 'localhost'
int porta = (this.args.size() > 0 && this.args[1].isInteger()) ? this.args[1].toInteger() : 2000
String apelidoJogador = this.args.size() > 2 ? this.args[2] : "Cliente-${new Date().time}"



println args.size()
println this.args.class
println porta

import logica.GerenciadorCliente

GerenciadorCliente cl = new GerenciadorCliente(endereco, porta, apelidoJogador)