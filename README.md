## Endpoint

### `POST /generate-qrcode`

Gera um QR Code no formato SVG a partir do conteúdo e tamanho fornecidos no corpo da requisição.

**Método:** `POST`

**URL:** `/generate-qrcode`

**Tipo de Conteúdo:** `application/json`

### 📥 Request Body

O corpo da requisição deve ser um objeto JSON contendo o texto a ser codificado e o tamanho desejado para o lado do QR Code em pixels.

| Campo | Tipo | Descrição | Obrigatório | Exemplo |
| :--- | :--- | :--- | :--- | :--- |
| `content` | `String` | O texto ou URL que será codificado no QR Code. | Sim | `"https://www.exemplo.com"` |
| `size` | `Integer` | O tamanho (lado) desejado do QR Code em pixels. | Sim | `256` |

**Exemplo de Requisição:**

```json
{
  "content": "<svg>exemplo</svg",
  "size": 300
}
