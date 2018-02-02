package com.project.school.buildingexample;

import android.os.Bundle;

import eu.kudan.kudan.ARAPIKey;
import eu.kudan.kudan.ARActivity;
import eu.kudan.kudan.ARImageTrackable;
import eu.kudan.kudan.ARImageTracker;
import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;

public class MainActivity extends ARActivity {

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
        modelNode.rotateByDegrees(90,1,0,0);
        modelNode.scaleByUniform(0.3f);

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

// Add model node to image trackable
        trackable.getWorld().addChild(modelNode);
    }
}
