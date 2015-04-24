#version 330 core

in vec3 vertexColor;
in vec2 textureCoord;
in vec3 vertexNormal;

in vec3 vertexPos;

out vec4 fragColor;


struct DirectionalLight 
{ 
   vec3 vertexColor; 
   vec3 vertexDirection; 
   float fAmbientIntensity; 
}; 
struct PointLight 
{ 
   vec3 vertexColor; 
   vec3 vertexPosition;
   float fAmbientIntensity; 
   float fConstantAttenuation;
   float fLinearAttenuation;
    float fQuadraticAttenuation;
}; 


uniform DirectionalLight moodLight; 
uniform PointLight orbLight; 

uniform sampler2D texImage;
uniform float alpha;
void main() {
    vec4 textureColor = texture(texImage, textureCoord);

    //directional, mood, light
    float fMoodDiffuseIntensity = max(0.0, dot(normalize(vertexNormal), -moodLight.vertexDirection)); 
    vec4 vMoodLight = vec4(moodLight.vertexColor*max(1.0,moodLight.fAmbientIntensity+fMoodDiffuseIntensity), 1.0);

    //point, orb,  light
    vec3 posToOrb = vertexPos-orbLight.vertexPosition;
    
    float distToOrb=length(posToOrb);
    posToOrb=normalize(posToOrb);
    float fOrbLightDiffuseIntensity = max(0.0, dot(normalize(vertexNormal), -posToOrb)); 

    float totalAttenuation =orbLight.fConstantAttenuation+distToOrb*orbLight.fLinearAttenuation+orbLight.fQuadraticAttenuation*distToOrb*distToOrb;
    vec4 vOrbLight = vec4(orbLight.vertexColor,1.0)*((orbLight.fAmbientIntensity+fOrbLightDiffuseIntensity)/totalAttenuation);

    vec4 color = textureColor *  vec4(vertexColor,1) * vMoodLight * vOrbLight;
    //apply alpha afterwards
    fragColor =  color * vec4(1,1,1, alpha);
}