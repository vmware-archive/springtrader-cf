/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.nanotrader.data.cloud;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.nanotrader.data.domain.Holding;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Gary Russell
 * @author Brian Dussault
 */

@Repository
public interface HoldingRepository {

    @RequestLine("GET /holdings/{id}")
    Holding find(@Param(value = "id") Long id);

    @RequestLine("POST /holdings/")
    @Headers("Content-Type: application/json")
    Holding save(@RequestBody Holding holding);

    @RequestLine("DELETE /holdings/{id}")
    @Headers("Content-Type: application/json")
    void delete(@Param(value = "id") Long id);

    @RequestLine("GET /holdings/accounts/{accountId}")
    List<Holding> findByAccountid(@Param(value = "accountId") Long accountId);

    @RequestLine("GET /holdings")
    List<Holding> findAll();
}
