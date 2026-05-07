package ca.maximilian.create.rockets.content.blocks.thruster;

public record ThrusterStats(
        double thrust,
        double airflow,
        float windUpTime,
        float windDownTime
) {}
