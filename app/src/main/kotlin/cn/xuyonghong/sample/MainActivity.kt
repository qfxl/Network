/*
 * Copyright (C)  XU YONGHONG, Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.xuyonghong.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.xuyonghong.network.download.DownloadClient
import cn.xuyonghong.network.download.ProgressListener
import cn.xuyonghong.sample.databinding.ActivityMainBinding
import cn.xuyonghong.sample.viewmodel.SampleViewModel
import cn.xuyonghong.sample.viewmodel.SampleViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding

    private val viewModel: SampleViewModel by viewModels {
        SampleViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.apply {
            btnGet.setOnClickListener {
                network {
                    viewModel.getBanner()
                        .request(onStart = {
                            showLoading()
                        }, onComplete = {
                            hideLoading()
                        }).response {
                            setResult(it)
                        }
                }
            }
            btnPost.setOnClickListener {
                network {
                    viewModel.login()
                        .request(onStart = {
                            showLoading()
                        }, onComplete = {
                            hideLoading()
                        }).response {
                            setResult(it)
                        }
                }
            }
            btnDownload.setOnClickListener {
                val destFile = File(filesDir, "test.txt")
                DownloadClient.get()
                    .download("https://www.baidu.com/", destFile, object : ProgressListener {
                        override fun onProgress(progress: Long, total: Long, done: Boolean) {
                            val percent = (100 * progress) / total
                            setResult("$percent %")
                        }
                    }, object : DownloadClient.OnDownloadListener {
                        override fun onDownloadSuccess(file: File?) {
                            showLoading()
                            lifecycleScope.launch(Dispatchers.IO) {
                                val result = String(destFile.readBytes())
                                withContext(Dispatchers.Main) {
                                    hideLoading()
                                    setResult(result)
                                }
                                withContext(Dispatchers.IO) {
                                    if (destFile.delete()) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "file deleted",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }

                        override fun onDownloadFailed(errorMsg: String?) {
                            Toast.makeText(
                                this@MainActivity,
                                "Download failed $errorMsg",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
            btnUpload.setOnClickListener {

            }
        }
    }

    private fun <T> setResult(t: T) {
        _binding.tvMainResult.text = t.toString()
    }

    private fun showLoading() {
        _binding.pbMainResult.visibility = View.VISIBLE
        _binding.tvMainResult.visibility = View.GONE
    }

    private fun hideLoading() {
        _binding.pbMainResult.visibility = View.GONE
        _binding.tvMainResult.visibility = View.VISIBLE
    }
}