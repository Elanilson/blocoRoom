package br.com.apkdoandroid.blocoroom.domain.repository

import br.com.apkdoandroid.blocoroom.data.entities.Categoria
import br.com.apkdoandroid.blocoroom.data.repository.ResultadoOperacao

interface CategoriaRepository {
   suspend fun salvar( categoria: Categoria ) : ResultadoOperacao
   suspend fun listar() : List<Categoria>
}