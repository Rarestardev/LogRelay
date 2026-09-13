package com.rarestardev.logrelay.state

enum class LogTags(val log: String) {
    V("VERBOSE"),
    D("DEBUG"),
    I("INFO"),
    W("WARN"),
    E("ERROR"),
    WTF("ASSERT")
}