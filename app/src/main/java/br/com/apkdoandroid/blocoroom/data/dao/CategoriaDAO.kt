package br.com.apkdoandroid.blocoroom.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.apkdoandroid.blocoroom.data.entities.Categoria

@Dao
interface CategoriaDAO {

    @Insert
    fun insert(categoria: Categoria) : Long
    @Update
    fun update(categoria: Categoria) : Int
    @Delete
    fun delete(categoria: Categoria) : Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categorias: List<Categoria>)

    @Query("SELECT * FROM categorias")
    fun listar() : List<Categoria>
}