package to.increment.llama

class NativeLib {

    /**
     * A native method that is implemented by the 'llama' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(modelPath: String, prompt: String, callback: CallbackHandler): String

    companion object {
        // Used to load the 'llama' library on application startup.
        init {
            System.loadLibrary("llama")

            // Create bin directory
        }
    }
}