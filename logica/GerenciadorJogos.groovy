package logica

import groovy.json.JsonBuilder
import basico.ObjetoBase
import dominio.*

class GerenciadorJogos extends ObjetoBase {
    ServerSocket servidor
    List<Usuario> usuarios
    List<Partida> partidas
    List<Convite> convites

    List<Thread> servicos


    GerenciadorJogos(int portaServidor, Console console = null) {
        usuarios = [].asSynchronized()
        partidas = []
        convites = []
        servicos = []

        /*Iniciando serviço que gerencia conexões*/
        servicos << this.iniciarGerenciaConexoes()

        if (console)
            servicos << this.iniciarBash(console)

        servidor = new Servidor(portaServidor)
        while(true) {
            servidor.accept { Socket socket ->
                Usuario usuario
                try{
                    println "processando nova conexão..."
                    socket.keepAlive = true
                    usuario = new dominio.Usuario(
                            socket: socket,
                            nome: socket.toString(),
                            apelido: "jogador${usuarios.size()}",
                            ip: socket.inetAddress.hostAddress
                    )
                    usuarios << usuario
                    socket.withStreams { InputStream input, OutputStream output ->
                        /*TODO colocar lógica de atualizacao no usuário p/ qualquer ação do mesmo */

                        while (true) {
                            def reader = input.newReader()
                            def buffer

                            buffer = reader.readLine()
                            boolean encerrar = processarMensagem(usuario, buffer)
                            if (encerrar) 
                                break;
                        }
                        //def now = new Date()
                        //output << "echo-response($now): " + buffer + "\n"
                    }
                    
                }catch (e){
                    println "Erro na conexão do usuário=$usuario socket=$socket"
                    fecharConexao(usuario)
                }
            }
        }
    }

    void destroy() {
        servicos*.interrupt()
        servidor.close()


    }

    Thread iniciarBash(final Console console) {

        return Thread.startDaemon {
            while(true) {
                String comando = console.readLine("Entre com  um comando (? para listar comandos): \n")
                println comando
                processarComando(comando)
            }
        }
    }

    def processarComando(String comando) {
        //To change body of created methods use File | Settings | File Templates.
        String[] commandos = comando.split(' ')
        switch (commandos.first().toUpperCase()){
            case 'Q':
                destroy()
            break

            case 'S':
                printStatus()
            break


            default: printComandos()




        }
    }

    void printComandos() {
        println '\n' * 5
        println "*" * 60
        println 's ou status - Ṕara ver status do servidor...'
        println "*" * 60

        println '\n' * 5
    }

    Thread iniciarGerenciaConexoes(){
        /*Iniciando serviço que gerencia conexões*/
        final long tempoGerenciaConexao = 15000
        final long tempoQuedaUsuario = 8000 + tempoGerenciaConexao
        final long tempoDerrubarUsuario = tempoQuedaUsuario * 2
        return Thread.startDaemon {
            while(true){
                sleep(tempoGerenciaConexao)
                Date dataHora = new Date()
                List<Usuario> usuariosComProblema = usuarios.findAll{ Usuario u -> (dataHora.time - u.dtUltimaMsg.time) > tempoQuedaUsuario }

                usuariosComProblema.each { Usuario usuario ->
                    Long tempoMiliSegSemAtualizacao = dataHora.time - usuario.dtUltimaMsg.time
                    Socket socket = usuario.socket
                    if (tempoMiliSegSemAtualizacao > tempoDerrubarUsuario){
                        fecharConexao(usuario)
                    } else {
//                        OutputStream output = socket.outputStream
//                        output << 'ACK|'
//                        output.flush()
//
                        enviarKeepAlive(usuario)
                        /*TODO não é necessário implementar o retorno pois o mesmo já é */
                    }
                }
                //printStatus()
            }

        }
    }

    boolean processarMensagem(Usuario usuario, String mensagem) {
        println "recebendo mensagem $usuario: $mensagem"


        boolean retorno = false

        usuario.dtUltimaMsg = new Date()

        if (mensagem) {
            Map<String, Object>  mapMessage = deserialize(mensagem)
            switch(mapMessage.COMMAND) {
                case Protocolo.CONNECT:
                    configurarUsuario(usuario, mapMessage.OBJECT)
                    break

                case Protocolo.CLOSE: 
                    retorno = true              
                    fecharConexao(usuario)
                break
                
                case Protocolo.KEEPALIVE: 
                    retorno = false
                    enviarKeepAliveResp(usuario)
                    
                break
                    
                default: println "COMMAND NÃO RECONHECIDO"
                break
            }
        } else {

            retorno = true
        }

        return retorno

        

        usuario.dtUltimaMsg = new Date()

        return false


    }

    void configurarUsuario(Usuario usuario, Map<String, Object> propriedades) {

        usuario.apelido = propriedades.USUARIO

    }

    void enviarMensagem(Usuario usuario, String mensagem) {
        try{
            println "enviarMensagem - $usuario - $mensagem"
            OutputStream output = usuario.socket.outputStream
            output << "$mensagem\n"
            output.flush()
        } catch(e) {
            /*TODO implementar lógica do que fazer no caso de erro de mensagem ao usuário*/
            println "falha na conexão do usuario $usuario"
        }

        // finally {
        //    fecharConexao(usuario)
        //}

    }


    void enviarKeepAlive(Usuario usuario) {

        String msg = serialize(
                [
                    COMMAND: Protocolo.KEEPALIVE,
                    OBJECT: [DATETIME: new Date().time]
                ]
        )
        enviarMensagem(usuario, msg)
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

    void fecharConexao(Usuario usuario) {  
        try{      
            Socket socket = usuario.socket
            if (socket && !socket.closed && socket.connected) {
                String msg = serialize([
                        COMMAND: Protocolo.CLOSE,
                        OBJECT: [
                                DATETIME: new Date().time
                        ]
                ])
                enviarMensagem(usuario, msg)            
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



    void printStatus() {
        println "************SERVER STATUS ****************"
        println "Usuarios conectados: ${usuarios}"
        println "Convitess: ${convites}"
        println "Partida: ${partidas}"
        println "************SERVER STATUS ****************"

        println '\n' * 4
    }


    void jogar(Usuario usuario, String parametros){
    }



}