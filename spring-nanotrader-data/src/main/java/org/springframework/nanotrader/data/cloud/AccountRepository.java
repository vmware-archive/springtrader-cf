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
import org.springframework.nanotrader.data.domain.Account;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface AccountRepository {

    @RequestLine("GET /accounts/{id}")
    Account findOne(@Param(value = "id") Long id);

    @RequestLine("GET /accounts/profile/{profileId}")
    Account findByProfileId(@Param(value = "profileId") Long id);

    @RequestLine("DELETE /accounts/")
    @Headers("Content-Type: application/json")
    void delete(@RequestBody Account account);

    @RequestLine("POST /accounts/")
    @Headers("Content-Type: application/json")
    Account save(@RequestBody Account account);

}
