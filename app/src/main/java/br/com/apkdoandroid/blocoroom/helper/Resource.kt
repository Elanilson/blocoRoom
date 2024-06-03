package br.com.apkdoandroid.blocoroom.helper

/**
 * Classe selada (sealed class) que representa o resultado de uma operação assíncrona.
 * O resultado pode ser bem-sucedido (Success) ou conter um erro (Error).
 * A classe é genérica e pode ser parametrizada com qualquer tipo de dado (T).
 *
 * @param data Os dados bem-sucedidos da operação. Pode ser nulo se a operação resultar em erro.
 * @param message A mensagem de erro. Pode ser nula se a operação for bem-sucedida.
 *
 * @see Success
 * @see Error
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    /**
     * Subclasse interna que representa um resultado bem-sucedido de uma operação assíncrona.
     * @param data Os dados bem-sucedidos da operação.
     */
    class Success<T>(data: T?) : Resource<T>(data)

    /**
     * Subclasse interna que representa um resultado contendo um erro de uma operação assíncrona.
     * @param message A mensagem de erro descrevendo o motivo do erro.
     * @param data Os dados associados ao erro, se houver. Pode ser nulo.
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}