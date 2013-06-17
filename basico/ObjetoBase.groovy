package  br.com.btoffoli.jogoDeTurno

class ObjetoBase {
//    Long id
    String id
    Date dtCriacao
    Date dtFinalizacao

    ObjetoBase() {
        id = UUID.randomUUID() as String
        dtCriacao = new Date()
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ObjetoBase that = (ObjetoBase) o

        if (dtCriacao != that.dtCriacao) return false
        if (dtFinalizacao != that.dtFinalizacao) return false
        if (id != that.id) return false
        if (to_s != that.to_s) return false

        return true
    }

    int hashCode() {
        int result
        result = id.hashCode()
        result = 31 * result + dtCriacao.hashCode()
        result = 31 * result + dtFinalizacao.hashCode()
        result = 31 * result + to_s.hashCode()
        return result
    }

    def to_s
    "<class:#{self.class} id:#{@id} - criacao:#{@criacao}>"
    end
}