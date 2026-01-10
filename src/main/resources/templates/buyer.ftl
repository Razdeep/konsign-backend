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
        <th>Bill no</th>
        <th>Bill date</th>
        <th>Bill amount</th>
        <th>Supplier name</th>
        <th>Voucher no</th>
        <th>Collected</th>
        <th>Due</th>
        <th>Bank</th>
        <th>DD no</th>
        <th>DD date</th>
    </tr>
    <tbody>
        <#list items as item>
        <tr>
            <td>${item.billNo!""}</td>
            <td>${item.billDate!""}</td>
            <td style="text-align: right;">${item.billAmount!""}</td>
            <td>${item.supplierName!""}</td>
            <td style="text-align: right;">${item.voucherNo!""}</td>
            <td style="text-align: right;">${item.amountCollected!""}</td>
            <td style="text-align: right;">${item.collectionDue!""}</td>
            <td>${item.bank!""}</td>
            <td style="text-align: right;">${item.ddNo!""}</td>
            <td>${item.ddDate!""}</td>
        </tr>
        </#list>
    </tbody>
</table>
</body>
</html>