package br.com.apkdoandroid.blocoroom.data.repository

import br.com.apkdoandroid.blocoroom.data.dao.AnotacaoDAO
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.relacionamentos.AnotacaoECategoria
import br.com.apkdoandroid.blocoroom.domain.repository.AnotacaoRepository
import javax.inject.Inject

class AnotacaoRepositoryImpl @Inject constructor(
    private val anotacaoDAO: AnotacaoDAO,
) : AnotacaoRepository {
    override suspend fun salvar( anotacao: Anotacao ): ResultadoOperacao {
        val idAnotacao = anotacaoDAO.salvar( anotacao )//-1 erro > 0 Sucesso
        if( idAnotacao > 0 ){
            return ResultadoOperacao(
                true, "Anotação cadastrada com sucesso"
            )
        }
        return ResultadoOperacao(
            true, "Erro ao cadastrar Anotação"
        )
    }

    override suspend fun atualizar(anotacao: Anotacao): ResultadoOperacao {
        val qtdRegistros = anotacaoDAO.atualizar( anotacao )
        if( qtdRegistros > 0 ){
            return ResultadoOperacao(
                true, "Anotação atualizada com sucesso"
            )
        }
        return ResultadoOperacao(
            true, "Erro ao atualizar Anotação"
        )
    }

    override suspend fun remover(anotacao: Anotacao): ResultadoOperacao {
        val qtdRegistros = anotacaoDAO.remover( anotacao )
        if( qtdRegistros > 0 ){
            return ResultadoOperacao(
                true, "Anotação removida com sucesso"
            )
        }
        return ResultadoOperacao(
            true, "Erro ao remover Anotação"
        )
    }

    override suspend fun listarAnotacaoECategoria(): List<AnotacaoECategoria> {
        return anotacaoDAO.listarAnotacaoECategoria()
    }

    override suspend fun pesquisarAnotacaoECategoria(texto: String): List<AnotacaoECategoria> {
        return anotacaoDAO.pesquisarAnotacaoECategoria(texto)
    }


}