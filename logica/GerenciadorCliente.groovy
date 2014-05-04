package logica

import dominio.Usuario 

class GerenciadorCliente {
	String endereco
	int porta
	Usuario usuario

	GerenciadorCliente(String endereco, int porta, String apelido) {
		this.endereco = endereco
		this.porta = porta
		this.usuario = new Usuario(apelido: apelido)

		this.iniciarConexao()

	}

	private void iniciarConexao() {

		if (usuario.socket)
			fecharConexao()

		Socket socket = usuario.socket = new Socket(endereco, porta)

		//Thread.startDaemon
		enviarMensagemConexao()
		socket.withStreams { inStream, outStream ->
  			//outStream << "Hello test server"  // send request first
  			while(true) {
	  			def reader = inStream.newReader()
	  			String mensagem = reader.readLine()      
	  			boolean encerrar = processarMensagem(mensagem)

	  			if (encerrar)
	  				break


	  		}
		}

	}

	private boolean processarMensagem(String mensagem) {
		println "mensagem - $mensagem"

		boolean retorno = false

		usuario.dtUltimaMsg = new Date()

		if (mensagem) {
			Map<String, Object>  mapMessage = deserialize(mensagem)
			switch(mapMessage.COMMAND) {
				case Protocolo.CLOSE: 
					retorno = true				
					iniciarConexao()
				break;
				
				case Protocolo.KEEPALIVE: 
					retorno = false
					enviarKeepAlive()
					
				break;
					
				break
			}
		} else {

			retorno = true
		}

		return retorno
	}

	private void enviarMensagem(String mensagem) {
		
		try{
            println "enviarMensagem - $usuario - $mensagem"
            OutputStream output = usuario.socket.outputStream
            output << "$mensagem\n"
            output.flush()
        } catch(e) {
            /*TODO implementar lógica do que fazer no caso de erro de mensagem ao usuário*/
            println "falha na conexão do usuario $usuario"
        }

	}

	
	void enviarMensagemConexao() {

        String msg = serialize(
                [
                    COMMAND: logica.Protocolo.CONNECT,
                    OBJECT: [
                    	DATETIME: new Date().time,
                    	USUARIO: this.usuario.apelido
                    ]
                ]
        )
        enviarMensagem(msg)
    }	

    void enviarKeepAlive() {

        String msg = serialize(
                [
                    COMMAND: Protocolo.KEEPALIVE,
                    OBJECT: [DATETIME: new Date().time]
                ]
        )
        enviarMensagem(msg)
    }

    void enviarKeepAliveResp(Usuario usuario) {

        String msg = serialize(
                [
                        COMMAND: Protocolo.KEEPALIVERESP,
                        OBJECT: [DATETIME: new Date().time]
                ]
        )
        enviarMensagem(usuario, msg)
    }


	void fecharConexao() {  
        try{      
            Socket socket = usuario.socket
            if (socket && !socket.closed && socket.connected) {
                String msg = serialize([
                        COMMAND: Protocolo.CLOSE,
                        OBJECT: [
                                DATETIME: new Date().time
                        ]
                ])
                enviarMensagem(msg)            
                socket.close()   
            }
        } catch (e) {
            println "fecharConexao - conexão do $usuario já estava fechada"
        }

        usuarios.remove(usuario)
          
                 
    }

	

	private String serialize(Map m) {
        // serialize
        //m = [a: 123, b: 'test']
        //JsonBuilder builder = new JsonBuilder()
        //builder(m)
        return m.inspect()
        //println builder.toString()
    }

    private def deserialize(String msg) {
        return Eval.me(msg)
    }

}