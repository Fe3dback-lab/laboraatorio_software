#include <SFML/Graphics.hpp>
#include <iostream>
using namespace sf;
using namespace std;

int main() {
    // Crear la textura de render 
    RenderTexture renderTexture({800, 600});

    // Limpiar con fondo blanco
    renderTexture.clear(Color::White);

    // --- Círculo rojo ---
    CircleShape circle(100.f);  // radio
    circle.setFillColor(Color::Red);
    circle.setPosition(Vector2f(100.f, 100.f));  // usar Vector2f
    renderTexture.draw(circle);

    // --- Rectángulo verde ---
    RectangleShape rectangle(Vector2f(200.f, 100.f));
    rectangle.setFillColor(Color::Green);
    rectangle.setPosition(Vector2f(300.f, 200.f));
    renderTexture.draw(rectangle);

    // --- Línea azul (como rectángulo delgado) ---
    RectangleShape line(Vector2f(300.f, 5.f));
    line.setFillColor(Color::Blue);
    line.setPosition(Vector2f(250.f, 400.f));
    line.setRotation(degrees(30.f));  // usar degrees() en SFML 3
    renderTexture.draw(line);

    // Finalizar dibujo
    renderTexture.display();

    // Copiar imagen final
    Image finalImage = renderTexture.getTexture().copyToImage();

    // Guardar como JPG
    if (!finalImage.saveToFile("resultado.jpg")) {
        cerr << "Error al guardar la imagen." << endl;
        return -1;
    }

    cout << "Imagen guardada como resultado.jpg" << endl;
    return 0;
}
