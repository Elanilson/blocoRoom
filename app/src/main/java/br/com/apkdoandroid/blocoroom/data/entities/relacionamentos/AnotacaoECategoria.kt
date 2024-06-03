package br.com.apkdoandroid.blocoroom.data.entities.relacionamentos

import androidx.room.Embedded
import androidx.room.Relation
import br.com.apkdoandroid.blocoroom.data.entities.Anotacao
import br.com.apkdoandroid.blocoroom.data.entities.Categoria


data class AnotacaoECategoria(
    @Embedded
    val anotacao: Anotacao,
    @Relation(
        entityColumn = "id_categoria",
        parentColumn = "id_categoria"
    )
    val categoria: Categoria
)

/*

1 título: Lista de compras categoria: Mercado  descrição: desc

* */