package com.thewind.main.vm

import com.thewind.widget.model.NoticeContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainPageViewModel {

    private val _noticeState: MutableStateFlow<NoticeContent?> = MutableStateFlow(null)

    val noticeState = _noticeState.asStateFlow()

    val tabs: List<String> = listOf("特征码搜索", "字符串搜索", "数据库解析", "字节码修改", "Intellij补丁")

    fun updateNotice(noticeContent: NoticeContent?) {
        _noticeState.value = noticeContent
    }


    companion object {
        val INSTANCE = MainPageViewModel()
    }
}