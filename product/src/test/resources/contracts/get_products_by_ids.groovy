package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "get product by ids"
    request {
        method GET()
        url '/v1/products?ids=1,2'
        headers {
            contentType('application/json')
        }
    }
    response {
        status OK()
        body(file("productList.json"))
        headers {
            contentType('application/json')
        }
    }
}
