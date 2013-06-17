class GerenciadorJogos extends ObjetoBase {
    Servidor servidor
    List<Usuario> usuarios
    List<Partida> partidas
    List<Convite> convites

    GerenciadorJogos(int portaServidor) {
        servidor = new Servidor(portaServidor)
        usuarios = []
        partidas = []
        convites []
    }


}