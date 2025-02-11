/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.runtime.operators.process;

import org.apache.flink.streaming.api.operators.Output;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.data.utils.JoinedRowData;
import org.apache.flink.table.data.utils.ProjectedRowData;
import org.apache.flink.table.types.inference.SystemTypeInference;

/**
 * Forwards input partition keys in sync with the output strategy of {@link SystemTypeInference}.
 */
public class PassPartitionKeysCollector extends PassThroughCollectorBase {

    private final ProjectedRowData projectedInput;
    private final JoinedRowData joinedRowData;

    public PassPartitionKeysCollector(Output<StreamRecord<RowData>> output, int[] partitionKeys) {
        super(output);
        projectedInput = ProjectedRowData.from(partitionKeys);
        joinedRowData = new JoinedRowData();
    }

    void setInput(RowData input) {
        projectedInput.replaceRow(input);
    }

    @Override
    public void collect(RowData functionOutput) {
        joinedRowData.replace(projectedInput, functionOutput);
        super.collect(joinedRowData);
    }
}
