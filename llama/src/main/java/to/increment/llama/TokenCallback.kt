package to.increment.llama


interface CallbackHandler {
    // Declare the native method
    fun callbackMethod(message: String?)
}