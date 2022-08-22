package com.codewithmehdi.myofflinemap

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.codewithmehdi.myofflinemap.databinding.ActivityMainBinding
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    companion object{
        val BERLIN = LatLong(52.5200, 13.4050)
        val ALABAMA = LatLong(32.3182, 86.9023)
    }

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidGraphicFactory.createInstance(application)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)


        val contract = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result->
            result?.data?.data?.let{uri->
                openMap(uri)
            }
        }
        b.openMap.setOnClickListener{
            contract.launch(
                Intent(
                    Intent.ACTION_OPEN_DOCUMENT
                ).apply{
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
            )
        }
    }


    fun openMap(uri: Uri){
        b.map.mapScaleBar.isVisible = true
        b.map.setBuiltInZoomControls(true)
        val cache = AndroidUtil.createTileCache(
            this,
            "mycache",
            b.map.model.displayModel.tileSize,
            1f,
            b.map.model.frameBufferModel.overdrawFactor
        )

        val stream = contentResolver.openInputStream(uri) as FileInputStream

        val mapStore = MapFile(stream)

        val renderLayer = TileRendererLayer(
            cache,
            mapStore,
            b.map.model.mapViewPosition,
            AndroidGraphicFactory.INSTANCE
        )

        renderLayer.setXmlRenderTheme(
            InternalRenderTheme.DEFAULT
        )

        b.map.layerManager.layers.add(renderLayer)

        b.map.setCenter(BERLIN)
        b.map.setZoomLevel(10)
    }
}