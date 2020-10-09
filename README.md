# Uzuki

<p align="center">
  <img src="https://vignette.wikia.nocookie.net/kancolle/images/4/42/Uzuki_Valentine_Full.png/revision/latest?cb=20190208131724">
</p>

The ShipGirl Project, Uzuki; `(c) Kancolle`

> Based on my earlier project [Hiei](https://github.com/Deivu/Hiei)

## Features

✅ Easy to use

✅ Configurable

✅ Rest based API

✅ Automatic Updates

## Downloads

🔗 https://github.com/Deivu/Uzuki/releases

## Support

🔗 https://discord.com/invite/FVqbtGu (#development)

Ping or ask for @Sāya#0113

## How to host

> Download the latest version from [Github Releases](https://github.com/Deivu/Uzuki/releases)

> Copy a config from [examples](https://github.com/Deivu/Uzuki/tree/master/example) folder

> Run the server by doing `java -jar uzuki.jar`

## Rest Client Example
```js 
const Fetch = require('node-fetch');

class Kancolle {
    constructor() {
        this.baseURL = 'http://localhost:1024';
        this.auth = '1234';
    }

    searchShip(ship) {
        return this._fetch('/ship/search', ship);
    }

    _fetch(endpoint, q) {
        const url = new URL(endpoint, this.baseURL);
        url.search = new URLSearchParams({ q }).toString();
        return Fetch(url.toString(), { headers: { 'authorization': this.auth } })
            .then(data => data.json());
    }
}

const client = new Kancolle();
client.searchShip('uzuki')
    .then(data => data.json())
    .then(data => console.log(data));
```
> Made with ❤ by @Sāya#0113