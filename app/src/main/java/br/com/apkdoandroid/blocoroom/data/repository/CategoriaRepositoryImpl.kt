package br.com.apkdoandroid.blocoroom.data.repository

import br.com.apkdoandroid.blocoroom.data.dao.CategoriaDAO
import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import br.com.apkdoandroid.blocoroom.domain.repository.CategoriaRepository
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val categoriaDAO: CategoriaDAO
) : CategoriaRepository {

    override suspend fun salvar( categoria: Categoria ) : ResultadoOperacao {//sucesso: false mensagem:

        val idCategoria = categoriaDAO.insert( categoria )//-1 erro > 0 Sucesso
        if( idCategoria > 0 ){
            return ResultadoOperacao(
                true, "Categoria cadastrada com sucesso"
            )
        }
        return ResultadoOperacao(
            true, "Erro ao cadastradar categoria"
        )


    }

    override suspend fun listar(): List<Categoria> {
        return categoriaDAO.listar()
    }
}

