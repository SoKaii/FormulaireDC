#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_aubervilliers_orange_aubrecettage_Home_1activity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
