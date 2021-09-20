package cn.xuyonghong.sample.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.xuyonghong.network.exception.UnAuthorizedException
import cn.xuyonghong.network.exception.UnExpectedDataException
import cn.xuyonghong.sample.App
import cn.xuyonghong.sample.response.BaseResponse
import cn.xuyonghong.sample.response.STATUS_OK
import cn.xuyonghong.sample.response.STATUS_UN_AUTHORIZED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

/**
 * author : qfxl
 * e-mail : xuyonghong0822@gmail.com
 * time   : 2021/07/23
 * desc   :
 * version: 1.0
 */

open class BaseViewModel : ViewModel() {
    /**
     * start a request
     */
    fun <T> request(
        showError: Boolean = true,
        responseScope: suspend CoroutineScope.() -> BaseResponse<T>
    ) = flow {
        handleResponse(responseScope)
    }.catch { t ->
        //io thread
        handleRequestError(t, showError)
        //continue throw for business deal
        throw t
    }.flowOn(Dispatchers.IO)

    /**
     * handle network response
     * @param responseScope
     */
    private suspend fun <T> FlowCollector<T>.handleResponse(
        responseScope: suspend CoroutineScope.() -> BaseResponse<T>
    ) {
        val response = responseScope(viewModelScope)
        when (response.errorCode) {
            STATUS_OK -> {
                emit(response.data)
            }
            STATUS_UN_AUTHORIZED -> {
                //未授权
                throw UnAuthorizedException("Login expired")
            }
            else -> {
                throw UnExpectedDataException("UnExpectedData")
            }
        }
    }


    /**
     * 处理错误，连接层
     * @param e
     * @param showError 是否显示错误信息
     */
    private fun handleRequestError(e: Throwable, showError: Boolean) {
        val msg = when (e) {
            is SocketTimeoutException -> {
                "Connect Timeout"
            }
            is UnknownHostException, is ConnectException, is HttpException -> {
                "Cannot connect to server"
            }
            is ProtocolException, is CancellationException -> {
                null
            }
            is UnAuthorizedException -> {
                e.message
            }
            else -> {
                e.message
            }
        }
        msg?.let { m ->
            if (showError) {
                //switch to main thread
                viewModelScope.launch {
                    Toast.makeText(App.instance, m, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}