<html>
<head>
    <style>
        *{margin: 0px;padding: 0px;border: 0px}
        body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,code,form,fieldset,legend,input,textarea,p,blockquote,th,td,hr,button,article,aside,details,figcaption,figure,footer,header,hgroup,menu,nav,section {
            margin:0;
            padding:0;
        }
        body {

        }
        .div1{
            width: 800px;
            height: 540px;
            background-image: url(${bgImg});
            -webkit-background-size: cover;
            -moz-background-size: cover;
            -o-background-size: cover;
            background-size: 100% 100%;
        }
        .container {
            height: 540px;
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-wrap: wrap;
        }

        .text {
            text-align: center;
            position: absolute;
            top: 50%;
            left: 0;
            right: 0;
            transform: translateY(-50%);
            font-size: 42px;
            color: red;
            padding-left: 200px;
            padding-right: 200px;
            font-weight: 800;
        }
    </style>
</head>
<body>
<div class="div1">
    <div class="container">
        <p class="text">${text}</p>
    </div>
</div>
</body>
</html>
