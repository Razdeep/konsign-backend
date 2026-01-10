<html>
<head>
    <title>Satyam Agencies</title>
    <style>
      @page {
        size: A4 landscape;
        margin: 5mm;
      }

      body {
        font-family: Arial, sans-serif;
      }
    </style>
</head>
<body>
<h1>${buyerName!""}</h1>
<table>
    <tr>
        <th>bill no</th>
        <th>bill date</th>
        <th>bill amount</th>
        <th>supplier name</th>
        <th>voucher no</th>
        <th>amount collected</th>
        <th>collection due</th>
        <th>bank</th>
        <th>dd no</th>
        <th>dd date</th>
    </tr>
    <tbody>
        <#list items as item>
        <tr>
            <td>${item.billNo!""}</td>
            <td>${item.billDate!""}</td>
            <td>${item.billAmount!""}</td>
            <td>${item.supplierName!""}</td>
            <td>${item.voucherNo!""}</td>
            <td>${item.amountCollected!""}</td>
            <td>${item.collectionDue!""}</td>
            <td>${item.bank!""}</td>
            <td>${item.ddNo!""}</td>
            <td>${item.ddDate!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
</body>
</html>