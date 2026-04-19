# Mini Juego: Bridge Survivor

Prototipo inspirado en el estilo del video: avanzan hordas por carriles, el jugador dispara automáticamente y toma puertas de mejora/recompensa.

## Mecánicas implementadas

- **3 carriles** de movimiento lateral.
- **Disparo automático** con mejora por nivel.
- **Puertas/premios**:
  - `+Soldados`
  - `+Daño`
  - `+Cadencia`
  - `+Puntos`
  - `Peligro` (castigo)
- **Niveles con dificultad progresiva**:
  - Aumenta velocidad de enemigos.
  - Aumenta frecuencia de aparición.
  - Sube exigencia para sobrevivir.
- **Sistema de vidas** y pantalla de game over.

## Cómo ejecutar

Opción rápida con Python:

```bash
cd mini-juego-survivor
python3 -m http.server 8080
```

Luego abrir: `http://localhost:8080`

## Próximos pasos sugeridos

1. Reemplazar los gráficos temporales por los assets de `ASSETS_LIBRES.md`.
2. Agregar animaciones sprite-sheet.
3. Añadir mini-jefe cada 5 niveles.
4. Persistir récord con `localStorage`.
