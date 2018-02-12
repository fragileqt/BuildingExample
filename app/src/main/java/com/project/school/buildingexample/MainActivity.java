package com.project.school.buildingexample;

import android.os.Bundle;
import android.util.Log;

import com.jme3.math.Quaternion;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARArbiTrack;
import eu.kudan.kudan.ARArbiTrackListener;
import eu.kudan.kudan.ARGyroPlaceManager;
import eu.kudan.kudan.ARImageNode;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTrackableListener;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class MainActivity extends ARActivity implements ARArbiTrackListener {

    boolean firstRun = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARAPIKey key = ARAPIKey.getInstance();
        key.setAPIKey("LLTYKN8CSOhlIP9lXuXgBkICJ9GMQWE2j0lYaEC4HOmkACBSQYkLdmMLcbe+RP2hAYuHvlbwXZGnOhOWYnBtiTPDLLaCbIG01ZQUbecVHSFHS2TEkaU+9X9RFZPMu/Q8i4jc3icSq3xTHY3+Ux+ZK8FlKHelDV9yl9CZAbo63PdXiCIGIrtj5UTq7eNvrVht/ZPSy6m/Dg3l2byzROut9fUzslvSecOL5uwqhyPH0ecZQ8fuUPiogBUbSoak0BeLJhgrjTiKu+nugqbqHBOpArkKymrJAtSh26dkc8hboiICBlQdW5swaVPZ/G4GT6DLxSAWEUrL7giORgo3lZQAkMkNR5ahi1XmkF+vF0ZoPhw8Y4lsrLG1flReWe5BEalovoo9DhQ0XRhWvZzB7RFRfjdJQrlJ77NKcZVBZp16Zo4wxJgou+o+GnC3t61tQ6AvNh8S/4NQ1Mm18GwGqklSz4+Zf/W3bFqzECyHN50ldws4S2ZdGT4SK0dHbIl8iLMMzEGlorhdiyyR1ungpbwe1JSDq4JFrXkpT8E9geZQPeKlAyHT9mQrymm8lbVTiWBZJX5Q1gRvLzMLpnXkA+LFUiyzHuGtdkWj3FkZQ7hVPgpKq29N3Q1orGXo6hFrGDRkBqDNqtnafnwEzNzjdpYLT4hypANZkXVAueQnZmRi8qM=");
        setContentView(R.layout.activity_main);

    }

    @Override
    public void setup()
    {
        super.setup();
        ARImageTrackable trackable = new ARImageTrackable("StarWars");
        trackable.loadFromAsset("target.png");

// Get instance of image tracker manager
        ARImageTracker trackableManager = ARImageTracker.getInstance();
        trackableManager.initialise();

// Add image trackable to image tracker manager
        trackableManager.addTrackable(trackable);

        ARModelImporter modelImporter = new ARModelImporter();
        modelImporter.loadFromAsset("ben.jet");
        ARModelNode modelNode = (ARModelNode)modelImporter.getNode();
        modelNode.rotateByDegrees(-90,0,0,1);
        modelNode.scaleByUniform(0.3f);
        modelNode.setName("BigBen");

// Load model texture
        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("bigBenTexture.png");

// Apply model texture file to model texture material and add ambient lighting
        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);
        material.setAmbient(0.8f, 0.8f, 0.8f);

// Apply texture material to models mesh nodes
        for (ARMeshNode meshNode : modelImporter.getMeshNodes()){
            meshNode.setMaterial(material);
        }

        // Initialise the image node with our image
        ARImageNode imageNode = new ARImageNode("hearth.png");
        imageNode.setName("Cow");
        imageNode.setPosition(1500,0,500);

// Add the image node as a child of the trackable's world
// Add model node to image trackable
        trackable.getWorld().addChild(modelNode);
        modelNode.addChild(imageNode);

        // Initialise ArbiTrack
        ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
//Add the activity as an ArbiTrack delegate
        arbiTrack.initialise();

        arbiTrack.addListener(this);
        arbiTrack.setTargetNode(trackable.getWorld());
        trackable.addListener(new ARImageTrackableListener() {
            @Override
            public void didDetect(ARImageTrackable arImageTrackable) {
                ARArbiTrack arbiTrack = ARArbiTrack.getInstance();
                arbiTrack.start();
            }

            @Override
            public void didTrack(ARImageTrackable arImageTrackable) {

            }

            @Override
            public void didLose(ARImageTrackable arImageTrackable) {

            }
        });
    }

    @Override
    public void arbiTrackStarted() {
            Log.e("Arbi","Arbitrack Started");
            if (firstRun)
            {
                ARImageTrackable legoTrackable = ARImageTracker.getInstance().findTrackable("StarWars");


                ARArbiTrack arbiTrack = ARArbiTrack.getInstance();


                ARModelNode benNode = (ARModelNode) legoTrackable.getWorld().findChildByName("BigBen");


                Quaternion benFullOrientation = benNode.getWorld().getWorldOrientation().mult(benNode.getWorldOrientation());
                benNode.setOrientation(arbiTrack.getWorld().getOrientation().inverse().mult(benFullOrientation));



                benNode.remove();

                arbiTrack.getWorld().addChild(benNode);

                firstRun = false;
            }
    }
}
