#include <jni.h>
#include "chat.h"
#include "token_callback.h"
#include <string>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

using TokenCallback = std::function<void(const std::string &)>;

extern "C" JNIEXPORT jstring JNICALL
Java_to_increment_llama_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject thiz,
        jstring modelPath,
        jstring prompt,
        jobject callback
) {
    // Setup Java callback
    auto cls = env->GetObjectClass(callback);
    auto callbackMethod = env->GetMethodID(cls, "callbackMethod", "(Ljava/lang/String;)V");
    if (callbackMethod == NULL) {
        std::string output = "can't find method";
        return env->NewStringUTF(output.c_str());
    }

    // Setup native callback
    TokenCallback tokenCallback = [env, callback, callbackMethod](const std::string &result) {
        auto str = result.c_str();
        auto message = env->NewStringUTF(str);
        env->CallVoidMethod(callback, callbackMethod, message);
        env->DeleteLocalRef(message);
    };
    
    if (NULL == modelPath) {
        std::string output = "failed to load file";
        return env->NewStringUTF(output.c_str());
    }

    auto fname = env->GetStringUTFChars(modelPath, nullptr);
    auto *promptStr = env->GetStringUTFChars(prompt, nullptr);
    char *args[5] = {
            "chat",
            "--prompt", strdup(promptStr),
            "--model", strdup(fname)
    };

    auto result = infer(tokenCallback, 5, args);

    return env->NewStringUTF(std::to_string(result).c_str());
}

// Load the library and the weights

// Expose a function call to run locally