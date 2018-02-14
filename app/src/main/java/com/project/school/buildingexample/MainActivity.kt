package com.project.school.buildingexample

import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.view.GestureDetector
import android.view.MotionEvent
import eu.kudan.kudan.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/*
TODO
    Zistit ako funguje GPS tracker v kudane, hlavne ako presne a či vôbec


 */
class MainActivity : ARActivity(), ARArbiTrackListener {

    private val intervalObservable = Observable.interval(6, TimeUnit.MILLISECONDS)
    private val gestureDetector by lazy {
        GestureDetectorCompat(this,object : GestureDetector.OnGestureListener {
            override fun onShowPress(p0: MotionEvent?) {
            }

            override fun onSingleTapUp(p0: MotionEvent?): Boolean {
                isRaining = !isRaining
                val benNode = ARArbiTrack.getInstance().world.findChildByName(ARObjects.BigBen.name)
                return true
            }

            override fun onDown(p0: MotionEvent?): Boolean {
                return true
            }

            override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                return true
            }

            override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
                val ben = ARArbiTrack.getInstance().world.findChildByName(ARObjects.BigBen.name).apply {
                    rotateByDegrees(p2-p3, 0f, 1f, 0f)
                }
                return true

            }

            override fun onLongPress(p0: MotionEvent?) {
            }
        });
    }
    private val trackableId = "Tracker"
    private var isRaining = false
    private val trackable by lazy {
        ARImageTrackable(trackableId).apply {
            loadFromAsset("target.png")
        }
    }

    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }
    override fun setup() {
        super.setup()
        with(ARImageTracker.getInstance()) {
            initialise()
            addTrackable(trackable)
        }

        val bigBen = createBigBen()
        val hearts = listOf(
            createHearth(1500f, 9000f, 500f),
            createHearth(-1500f, 6500f, 750f),
            createHearth(2000f, 1500f, -500f),
            createHearth(1250f, 3400f, -750f),
            createHearth(-1250f, 350f, 400f),
            createHearth(-2000f, 4500f, -500f),
            createHearth(-500f, 2500f, -750f),
            createHearth(1250f, 5350f, 400f),
            createHearth(1250f, 4600f, -400f),
            createHearth(-1200f, 7250f, 1500f)
        )
        val cloud = createCloud(0f,10000f,0f);

        bigBen.addChild(cloud)
        hearts.forEach(bigBen::addChild)

        trackable.world.addChildren(listOf(bigBen))
        ARGyroPlaceManager.getInstance().initialise()

        with(ARArbiTrack.getInstance()) {
            initialise()
            addListener(this@MainActivity)
            targetNode = trackable.world
        }

        trackable.addListener(arImageListener)
    }

    override fun arbiTrackStarted() {
        if (firstRun) {

            val trackable = ARImageTracker.getInstance().findTrackable(trackableId)
            val arbiTrack = ARArbiTrack.getInstance()

            val cloud = trackable.world.findChildByName(ARObjects.Cloud.name)
            val benNode = trackable.world.findChildByName(ARObjects.BigBen.name)

            val benOrientation = benNode.world.worldOrientation.mult(benNode.worldOrientation)
            benNode.orientation = arbiTrack.world.orientation.inverse().mult(benOrientation)

            arbiTrack.world.addChildren(listOf(benNode))

            intervalObservable.subscribe {
                benNode
                    .children
                    .filter { it.isHearth() }
                    .forEach { it.rainAnimation() }
            }

            firstRun = false
        }

    }

    private val arImageListener = object : ARImageTrackableListener {
        override fun didDetect(arImageTrackable: ARImageTrackable) {
            val arbiTrack = ARArbiTrack.getInstance()
            arbiTrack.start()
        }

        override fun didTrack(arImageTrackable: ARImageTrackable) {

        }

        override fun didLose(arImageTrackable: ARImageTrackable) {

        }

    }

    private fun createCloud(x: Float, y: Float, z: Float): ARModelNode {
        val modelImporter = ARModelImporter().apply {
            loadFromAsset("cloud.jet")

        }

        val texture2D = ARTexture2D()
        texture2D.loadFromAsset("clouds.png")

        val material = ARLightMaterial().apply {
            setAmbient(0.8f, 0.8f, 0.8f)
            setTexture(texture2D)
        }

        // Apply texture material to models mesh nodes
        modelImporter.meshNodes.forEach {
            it.material = material
        }

        return modelImporter.node.apply {
            name = ARObjects.Cloud.name
            setPosition(x, y, z)
            scaleByUniform(.5f)
        }

    }

    private fun createBigBen(): ARModelNode {
        val modelImporter = ARModelImporter()
        modelImporter.loadFromAsset("ben.jet")

        val modelNode = modelImporter.node.apply {
            rotateByDegrees(-90f, 0f, 0f, 0f)
            scaleByUniform(0.1f)
            setOrientation(1f, 1f, 1f, 1f)
            rotateByDegrees(-90f, 0f, 0f, 1f)
            name = ARObjects.BigBen.name
        }

        // Load model texture
        val texture2D = ARTexture2D()
        texture2D.loadFromAsset("bigBenTexture.png")

        // Apply model texture file to model texture material and add ambient lighting
        val material = ARLightMaterial().apply {
            setTexture(texture2D)
            setAmbient(0.8f, 0.8f, 0.8f)
        }

        // Apply texture material to models mesh nodes
        modelImporter.meshNodes.forEach {
            it.material = material
        }

        return modelNode
    }

    private fun createHearth(x: Float, y: Float, z: Float) = ARImageNode("heart.png").apply {
        name = ARObjects.Hearth.name
        setPosition(x, y, z)
        scaleByUniform(0.4f)
        visible = isRaining
    }

    private fun ARNode.isHearth() = name.contentEquals(ARObjects.Hearth.name)

    private fun ARNode.rainAnimation(incerment: Int = 10) {
        val x = position.getX()
        val y = position.getY()
        val z = position.getZ()
        if (y < 0){
            setPosition(x, 10000f, z)
            visible = isRaining
        }else
            setPosition(x, y - incerment, z)
    }

}

enum class ARObjects {
    Hearth, BigBen, Cloud
}