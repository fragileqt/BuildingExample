package com.project.school.buildingexample

import android.os.Bundle
import eu.kudan.kudan.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import com.jme3.math.Quaternion
import java.util.*


class MainActivity : ARActivity(), ARArbiTrackListener {

    private val intervalObservable = Observable.interval(6, TimeUnit.MILLISECONDS)
    private val trackableId = "StarWars"
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

    override fun setup() {
        super.setup()
        with(ARImageTracker.getInstance()) {
            initialise()
            addTrackable(trackable)
        }

        val bigBen = createBigBen()
        val hearts = listOf(
            createHearth(1500f, 2500f, 500f),
            createHearth(-1500f, -2500f, 750f),
            createHearth(2000f, 1500f, -500f),
            createHearth(1250f, 1400f, -750f),
            createHearth(-1250f, 350f, 400f),
            createHearth(-2000f, -3500f, -500f),
            createHearth(-500f, -5500f, -750f),
            createHearth(1250f, 350f, 400f),
            createHearth(1250f, -400f, -400f),
            createHearth(-1200f, 1250f, 1500f)
        )
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
            val benNode = trackable.world.findChildByName(ARObjects.BigBen.name).apply {
                setOrientation(1f, 1f, 1f, 1f)
                rotateByDegrees(-90f, 0f, 0f, 1f)
            }

            val benOrientation = benNode.world.worldOrientation.mult(benNode.worldOrientation)
            benNode.orientation = arbiTrack.world.orientation.inverse().mult(benOrientation)

            arbiTrack.world.addChildren(listOf(benNode))

            intervalObservable.subscribe {
                benNode
                    .children
                    .filter { it.isHearth() }
                    .forEach { it.goUpAnimation() }
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

        val material = ARLightMaterial().apply {
            setAmbient(0.8f, 0.8f, 0.8f)
        }

        // Apply texture material to models mesh nodes
        modelImporter.meshNodes.forEach {
            it.material = material
        }

        return modelImporter.node.apply {
            name = ARObjects.Cloud.name
            setPosition(x, y, z)
            scaleByUniform(10f)
        }

    }

    private fun createBigBen(): ARModelNode {
        val modelImporter = ARModelImporter()
        modelImporter.loadFromAsset("ben.jet")

        val modelNode = modelImporter.node.apply {
            rotateByDegrees(-90f, 0f, 0f, 0f)
            scaleByUniform(0.1f)
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

    }

    private fun ARNode.isHearth() = name.contentEquals(ARObjects.Hearth.name)

    private fun ARNode.goUpAnimation(incerment: Int = 10) {
        val x = position.getX()
        val y = position.getY()
        val z = position.getZ()
        if (y > 10000)
            setPosition(x, 0f, z)
        else
            setPosition(x, y + incerment, z)
    }

}

enum class ARObjects {
    Hearth, BigBen, Cloud
}