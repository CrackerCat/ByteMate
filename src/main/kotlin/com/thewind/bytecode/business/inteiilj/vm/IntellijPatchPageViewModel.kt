package com.thewind.bytecode.business.inteiilj.vm

import androidx.lifecycle.ViewModel
import com.thewind.bytecode.business.inteiilj.model.IntellijPatchPageUiState
import com.thewind.bytecode.business.inteiilj.patcher.IntellijGeneralPatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class IntellijPatchPageViewModel : ViewModel() {

    private val _pageState: MutableStateFlow<IntellijPatchPageUiState> = MutableStateFlow(IntellijPatchPageUiState())

    val pageState = _pageState.asStateFlow()


    fun updateInstallPath(path: String) {
        val data = _pageState.value
        _pageState.value = data.copy(installPath = path)
    }


    private fun updateNotice(notice: String) {
        val data = _pageState.value
        _pageState.value = data.copy(notices = data.notices.toMutableList().apply {
            add(notice)
        })
    }


    suspend fun patchLicenseDialog() = withContext(Dispatchers.IO) {
        if (_pageState.value.isPatching) {
            updateNotice("正在处理中，请勿重复点击")
            return@withContext
        }
        _pageState.value = _pageState.value.copy(notices = listOf("输入地址"), isPatching = true)
        IntellijGeneralPatcher.patchLicenseDialog(_pageState.value.installPath) {
            updateNotice(it)
        }
        _pageState.value = _pageState.value.copy(isPatching = false)
    }

}