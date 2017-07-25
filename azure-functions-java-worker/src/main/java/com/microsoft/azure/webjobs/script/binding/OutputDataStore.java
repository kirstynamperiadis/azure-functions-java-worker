package com.microsoft.azure.webjobs.script.binding;

import java.util.*;

import com.microsoft.azure.webjobs.script.*;
import com.microsoft.azure.webjobs.script.rpc.messages.*;

/**
 * A warehouse storing all the output binding data from the logic. You can use this class to generate a single specific binding. And use it to
 * convert to the actual bindings.
 */
public final class OutputDataStore {
    public List<ParameterBinding> toParameterBindings() {
        return Utility.map(this.outputs, OutputData::toParameterBinding);
    }

    public Optional<BindingData.Value<?>> tryGenerateReturn(Object retValue) {
        return this.addOutput(() -> RpcOutputData.parse(retValue));
    }

    public Optional<BindingData.Value<?>> tryGenerate(String name, Class<?> target) {
        return this.addOutput(() -> RpcOutputData.parse(name, target));
    }

    private Optional<BindingData.Value<?>> addOutput(OutputDataSupplier dataSupplier) {
        try {
            OutputData data = dataSupplier.get();
            this.outputs.add(data);
            return Optional.of(data.getValue());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @FunctionalInterface
    private interface OutputDataSupplier { RpcOutputData get() throws Exception; }

    private List<OutputData> outputs = new ArrayList<>();
}