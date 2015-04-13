#version 330 core

in vec3 position;
in vec3 color;
in vec2 texcoord;
in vec3 normal;

out vec3 vertexColor;
out vec2 textureCoord;
out vec3 vertexNormal; 

out vec3 vertexPos;

uniform mat4 modelview;
uniform mat4 projection;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
    mat4 mvp = projection * modelview;
    gl_Position =  mvp * vec4(position, 1.0);
    //pass the vertex position to the fragment shader for point light calculation
    vertexPos =  position;
    //The normal is not transformed because we don't scale or rotate cubes locally
    vertexNormal = normal;
}