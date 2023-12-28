//
// Created by Karn Saheb on 2023-12-27.
//

#ifndef LLAMA_TOKEN_CALLBACK_H
#define LLAMA_TOKEN_CALLBACK_H

#include <string>
#include <functional>

// Define the callback type
using TokenCallback = std::function<void(const std::string &)>;

#endif //LLAMA_TOKEN_CALLBACK_H
