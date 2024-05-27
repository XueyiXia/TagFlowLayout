package com.framework.viewbinding.base

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * @author: xiaxueyi
 * @date: 2022-09-23
 * @time: 15:46
 * @说明:
 */
abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    companion object{
        private const val TAG = "BaseActivity"
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if(newConfig.fontScale != 1.0F) {
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun  getResources(): Resources {
        var resources=super.getResources()
        if(resources.configuration.fontScale!=1.0f){
            val newConfig = Configuration()
            newConfig.fontScale = 1.0F
            val context = createConfigurationContext(newConfig)
            resources=context.resources
        }
        return resources
    }


    open lateinit var mViewBinding: T


    private lateinit var resultLauncher: ActivityResultLauncher<Intent>  // registerForActivityResult

    private var activityResultCallback: ActivityResultCallback<ActivityResult>?=null  //暴露给子页面的回调


    /**
     * 当前页面回调数据处理
     */
    private val launcherCallback = ActivityResultCallback<ActivityResult> { result ->
        val code = result.resultCode
        val data = result.data
        val msgContent = "code = $code msg = $data "
        activityResultCallback?.onActivityResult(result)

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "跳转界面--->>" + this.javaClass.simpleName)
        val superclass = javaClass.genericSuperclass
        if (superclass != null) {
            val aClass = (superclass as ParameterizedType).actualTypeArguments[0] as Class<*>
            try {
                val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
                mViewBinding = method.invoke(null, layoutInflater) as T
                setContentView(mViewBinding.root)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        initView(window.decorView, savedInstanceState)


        initRegisterForActivityResult()
    }


    /**
     * activity跳转（无参数）
     * @param className
     */
    open fun startActivity(className: Class<out Activity>) {
        val intent = Intent(this, className)
        startActivity(intent)
    }

    /**
     * activity跳转（有参数）
     * @param className
     */
    open fun startActivity(className: Class<out Activity>, bundle: Bundle) {
        val intent = Intent(this, className)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    /**
     * activity结果跳转（没有参数）
     * @param className
     * @param requestCode
     */
    open fun startActivityForResult(className: Class<out Activity>, activityResultCallback: ActivityResultCallback<ActivityResult>?) {
        this.activityResultCallback=activityResultCallback
        val intent = Intent(this, className)
        resultLauncher.launch(intent)
    }




    /**
     * activity结果跳转（有参数）
     * @param className
     * @param bundle
     * @param requestCode
     */
    open fun startActivityForResult(className: Class<out Activity>, bundle: Bundle,activityResultCallback: ActivityResultCallback<ActivityResult>?) {
        this.activityResultCallback=activityResultCallback
        val intent = Intent(this, className)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }


    /**
     * 注册页面回调
     */
    private fun initRegisterForActivityResult(){
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),launcherCallback)
    }


    /**
     * 入口函数
     */
    abstract fun initView(rootView: View, savedInstanceState: Bundle?)
}